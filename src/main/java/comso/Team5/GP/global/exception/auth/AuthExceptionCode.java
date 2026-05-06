package comso.Team5.GP.global.exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthExceptionCode {

    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, "사용자의 이메일이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    AuthExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
