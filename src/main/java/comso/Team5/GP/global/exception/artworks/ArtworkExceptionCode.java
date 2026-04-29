package comso.Team5.GP.global.exception.artworks;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArtworkExceptionCode {

    // Domain
    NOT_FOUND_ARTWORK(HttpStatus.NOT_FOUND,"해당 작품이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ArtworkExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
