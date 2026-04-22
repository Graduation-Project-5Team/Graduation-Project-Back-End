package comso.Team5.GP.global.exception.comments;

import lombok.Getter;

@Getter
public class CommentException extends RuntimeException {

    private final CommentExceptionCode errorCode;

    public CommentException(CommentExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
