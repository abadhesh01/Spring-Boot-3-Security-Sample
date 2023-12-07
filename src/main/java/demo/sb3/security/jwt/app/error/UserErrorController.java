package demo.sb3.security.jwt.app.error;

import demo.sb3.security.jwt.app.entity.error.SecuredUserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@SuppressWarnings("unused")
public class UserErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception exception, WebRequest request) {

        HttpStatus status;

        // Setting error status for application level errors.
        if (exception instanceof SecuredUserNotFoundException) status = HttpStatus.NOT_FOUND;
        else if (exception instanceof DataIntegrityViolationException) status = HttpStatus.CONFLICT;
        else if (exception instanceof NoResourceFoundException ||
                exception instanceof HttpRequestMethodNotSupportedException ||
                exception instanceof MethodArgumentTypeMismatchException)
            status = HttpStatus.BAD_REQUEST;
            // Setting error status for authentication and authorization related errors.
        else if (exception instanceof AccessDeniedException) status = HttpStatus.FORBIDDEN;
        else if (exception instanceof BadCredentialsException) status = HttpStatus.UNAUTHORIZED;
            // Setting error status for JWT(JSON Web Token) related errors.
            // [Condition: If authentication is JWT authentication]
        else if (exception instanceof MalformedJwtException) status = HttpStatus.NOT_ACCEPTABLE;
        else if (exception instanceof ExpiredJwtException) status = HttpStatus.IM_USED;
            // Setting error status for remaining errors.
        else status = HttpStatus.EXPECTATION_FAILED;


        return new ResponseEntity<>(generateErrorResponse(
                exception.getClass().getName(),
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now().toString()),
                status);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<Map<String, Object>> handleGlobalError(Error error, WebRequest request) {
        return new ResponseEntity<>(generateErrorResponse(
                error.getClass().getName(),
                error.getMessage(),
                request.getDescription(false),
                LocalDateTime.now().toString()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public Map<String, Object> generateErrorResponse(String error, String message, String request, String time) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("request", request);
        errorResponse.put("time", time);
        return errorResponse;
    }
}
