package comso.Team5.GP.global.exception.comments;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentExceptionCode {

    // Domain
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND,"해당 댓글이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    CommentExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
