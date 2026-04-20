package comso.Team5.GP.users.exception;

import comso.Team5.GP.users.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
        UserExceptionCode code = e.getErrorCode();
        ErrorResponse body = new ErrorResponse(code.name(), code.getMessage());
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }
}
