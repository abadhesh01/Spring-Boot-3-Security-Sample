package demo.sb3.security.jwt.app;

import demo.sb3.security.jwt.app.entity.SecuredUser;
import demo.sb3.security.jwt.app.entity.UserRole;
import demo.sb3.security.jwt.app.repository.SecuredUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Sb3JwtSecurityDemoAppApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Sb3JwtSecurityDemoAppApplication.class, args);
        SecuredUserRepository repository = applicationContext.getBean(SecuredUserRepository.class);
        if (repository.findByRole(UserRole.ADMIN).isEmpty()) {
            repository.save(SecuredUser.builder().username("admin").password("admin").role(UserRole.ADMIN).build());
        }
    }

}
