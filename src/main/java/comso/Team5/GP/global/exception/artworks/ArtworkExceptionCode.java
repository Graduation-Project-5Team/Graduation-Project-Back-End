package comso.Team5.GP.global.exception.artworks;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ArtworkExceptionCode {

    NOT_FOUND_ARTWORK(HttpStatus.NOT_FOUND, "해당 작품이 존재하지 않습니다."),
    FORBIDDEN_ARTWORK(HttpStatus.FORBIDDEN, "해당 작품에 대한 권한이 없습니다."),
    NOT_STUDENT(HttpStatus.FORBIDDEN, "학생만 작품을 등록할 수 있습니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 작품입니다."),
    READY_LIKE(HttpStatus.BAD_REQUEST, "좋아요를 누른 상태로 요청해야 가능한 서비스입니다."),
    USER_OR_ARTWORK_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자나 작품이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ArtworkExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
