package comso.Team5.GP.global.exception.artworks;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArtworkExceptionCode {

    NOT_FOUND_ARTWORK(HttpStatus.NOT_FOUND, "해당 작품이 존재하지 않습니다."),
    FORBIDDEN_ARTWORK(HttpStatus.FORBIDDEN, "해당 작품에 대한 권한이 없습니다."),
    NOT_STUDENT(HttpStatus.FORBIDDEN, "학생만 작품을 등록할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ArtworkExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
