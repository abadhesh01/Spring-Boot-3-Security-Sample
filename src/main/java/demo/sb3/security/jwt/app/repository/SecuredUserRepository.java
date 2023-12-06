package demo.sb3.security.jwt.app.repository;

import demo.sb3.security.jwt.app.dto.SecuredUserInfo;
import demo.sb3.security.jwt.app.entity.SecuredUser;
import demo.sb3.security.jwt.app.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecuredUserRepository extends JpaRepository<SecuredUser, UUID> {
    Optional<SecuredUser> getUserByUsername(String username);

    @Query("SELECT new demo.sb3.security.jwt.app.dto.SecuredUserInfo(su.id, su.username) FROM SecuredUser su")
    List<SecuredUserInfo> getAllSecuredUsersWithIdAndUsername();

    List<SecuredUserInfo> findByRole(UserRole role);
}
