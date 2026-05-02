package comso.Team5.GP.global.exception.users;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    AUTH_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "Authorization Bearer 토큰이 필요합니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "리프레시 토큰이 요청에 포함되지 않았습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 리프레시 토큰입니다. 다시 로그인해주세요.");

    private final HttpStatus httpStatus;
    private final String message;

    UserExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
