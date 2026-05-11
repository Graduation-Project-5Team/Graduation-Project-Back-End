package comso.Team5.GP.global.exception.auth;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final AuthExceptionCode errorCode;

    public AuthException(AuthExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
