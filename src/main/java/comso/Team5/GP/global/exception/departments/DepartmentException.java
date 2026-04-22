package comso.Team5.GP.global.exception.departments;

import lombok.Getter;

@Getter
public class DepartmentException extends RuntimeException {

    private final DepartmentExceptionCode errorCode;

    public DepartmentException(DepartmentExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
