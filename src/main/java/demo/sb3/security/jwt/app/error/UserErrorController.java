package demo.sb3.security.jwt.app.error;

import demo.sb3.security.jwt.app.entity.error.SecuredUserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        if (exception instanceof SecuredUserNotFoundException) status = HttpStatus.NOT_FOUND;
        else if (exception instanceof DataIntegrityViolationException) status = HttpStatus.CONFLICT;
        else if (exception instanceof NoResourceFoundException ||
                exception instanceof HttpRequestMethodNotSupportedException ||
                exception instanceof MethodArgumentTypeMismatchException)
            status = HttpStatus.BAD_REQUEST;
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
