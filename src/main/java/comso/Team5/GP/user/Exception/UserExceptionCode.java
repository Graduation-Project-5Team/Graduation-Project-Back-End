package comso.Team5.GP.user.Exception;

import lombok.Getter;

@Getter
public enum UserExceptionCode {

    // Domain
    MEMBER_NOT_FOUND("존재하지 않는 사용자 입니다.");

    private final String message;

    UserExceptionCode(String message) {
        this.message = message;
    }

}
