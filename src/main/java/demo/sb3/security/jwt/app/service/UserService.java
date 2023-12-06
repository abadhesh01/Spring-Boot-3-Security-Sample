package demo.sb3.security.jwt.app.service;

import demo.sb3.security.jwt.app.entity.SecuredUser;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public interface UserService {
    Map<String, Object> getCurrentUser(Principal principal);

    Map<String, Object> createUser(SecuredUser user);

    Map<String, Object> updateUserPassword(Map<String, String> password, Principal principal);

    Map<String, Object> deleteUser(UUID id, Principal principal);

    Map<String, Object> getUsersOnly();

    Map<String, Object> getAllUsers();

    default Map<String, Object> generateResponse(String message, Object content) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("content", content);
        return response;
    }
}
