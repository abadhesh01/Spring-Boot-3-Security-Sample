package demo.sb3.security.jwt.app.entity.error;

public class SecuredUserNotFoundException extends RuntimeException {

    public SecuredUserNotFoundException(String message) {
        super(message);
    }

    public SecuredUserNotFoundException(String fieldName, String fieldValue) {
        super("User with " + fieldName + "='" + fieldValue + "' was not found!");
    }
}
