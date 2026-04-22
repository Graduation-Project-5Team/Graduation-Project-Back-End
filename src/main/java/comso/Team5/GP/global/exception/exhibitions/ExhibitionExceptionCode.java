package comso.Team5.GP.global.exception.exhibitions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExhibitionExceptionCode {

    // Domain
    NOT_FOUND_EXHIBITION(HttpStatus.NOT_FOUND,"해당 전시를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExhibitionExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
