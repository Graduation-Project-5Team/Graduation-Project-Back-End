package comso.Team5.GP.global.exception.departments;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DepartmentExceptionCode {

    NOT_FOUND_NAME(HttpStatus.NOT_FOUND, "해당 학과를 찾을 수 없습니다."),
    FORBIDDEN_DEPARTMENT(HttpStatus.FORBIDDEN, "해당 학과에 대한 권한이 없습니다."),
    NOT_ADMIN(HttpStatus.FORBIDDEN, "관리자만 접근할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    DepartmentExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
