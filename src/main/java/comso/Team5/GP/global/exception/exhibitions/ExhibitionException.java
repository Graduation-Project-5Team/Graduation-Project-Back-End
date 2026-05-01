package comso.Team5.GP.global.exception.exhibitions;

import lombok.Getter;

@Getter
public class ExhibitionException extends RuntimeException {

    private final ExhibitionExceptionCode errorCode;

    public ExhibitionException(ExhibitionExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
