package demo.sb3.security.jwt.app.service;

import demo.sb3.security.jwt.app.entity.SecuredUser;
import demo.sb3.security.jwt.app.entity.UserRole;
import demo.sb3.security.jwt.app.entity.error.OperationNotAllowedException;
import demo.sb3.security.jwt.app.entity.error.SecuredUserNotFoundException;
import demo.sb3.security.jwt.app.entity.error.ValidationException;
import demo.sb3.security.jwt.app.repository.SecuredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@SuppressWarnings("unused")
public class UserServiceImpl implements UserService {

    @Autowired
    private SecuredUserRepository repository;

    @Override
    public Map<String, Object> getCurrentUser(Principal principal) {
        checkLoggedInUser(principal);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("CURRENT-USER", principal.getName());
        return response;
    }

    @Override
    public Map<String, Object> createUser(SecuredUser user) {
        usernameValidator(user);
        passwordValidator(user);
        user.setRole(UserRole.USER);
        return generateResponse("User with username='"
                        + user.getUsername()
                        + "' has been added to the database successfully.",
                this.repository.save(user));
    }

    @Override
    public Map<String, Object> updateUserPassword(Map<String, String> password, Principal principal) {
        checkLoggedInUser(principal);
        SecuredUser currentUser = this.repository.getUserByUsername(principal.getName())
                .orElseThrow(() -> new SecuredUserNotFoundException("username", principal.getName()));
        String updatedPassword = password.get("password");
        passwordValidator(SecuredUser.builder().password(updatedPassword).build());
        currentUser.setPassword(updatedPassword.replaceAll("\\s", ""));
        return generateResponse(
                "Your password has been updated successfully.",
                this.repository.save(currentUser));
    }

    @Override
    public Map<String, Object> deleteUser(UUID id, Principal principal) {
        checkLoggedInUser(principal);
        SecuredUser user = this.repository.findById(id)
                .orElseThrow(() -> new SecuredUserNotFoundException("id", id.toString()));
        if (user.getRole().equals(UserRole.ADMIN))
            throw new OperationNotAllowedException("User with id='" + id + "' cannot be deleted!");
        this.repository.deleteById(id);
        return generateResponse(
                "User with id='" + id + "' has been deleted successfully.",
                this.repository.findAll());
    }

    @Override
    public Map<String, Object> getUsersOnly() {
        return generateResponse("List of all Users with role USER only.",
                this.repository.findByRole(UserRole.USER));
    }

    @Override
    public Map<String, Object> getAllUsers() {
        return generateResponse("List of all Users.",
                this.repository.getAllSecuredUsersWithIdAndUsername());
    }

    private void usernameValidator(SecuredUser user) {
        Objects.requireNonNull(user.getUsername(), "Field 'username' cannot be NULL!");
        user.setUsername(user.getUsername().replaceAll("\\s", ""));
        if (user.getUsername().isEmpty())
            throw new ValidationException("Field 'username' cannot be EMPTY!");
        if (user.getUsername().length() < 3)
            throw new ValidationException("Field 'username' should have minimum 3 characters.");

    }

    private void passwordValidator(SecuredUser user) {
        Objects.requireNonNull(user.getPassword(), "Field 'password' cannot be NULL!");
        user.setPassword(user.getPassword().replaceAll("\\s", ""));
        if (user.getPassword().isEmpty())
            throw new ValidationException("Field 'password' cannot be EMPTY!");
        if (user.getPassword().length() < 3)
            throw new ValidationException("Field 'password' should have minimum 3 characters.");
    }

    private void checkLoggedInUser(Principal principal) {
        try {
            Objects.requireNonNull(principal);
        } catch (Exception exception) {
            throw new SecuredUserNotFoundException("No logged-in user found!");
        }
    }
}
