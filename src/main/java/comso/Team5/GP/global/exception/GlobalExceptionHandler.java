package comso.Team5.GP.global.exception;

import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.comments.CommentException;
import comso.Team5.GP.global.exception.departments.DepartmentException;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionException;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.users.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 각 도메인의 HTTP 상태와 메시지를 꺼내 ErrorResponse로 반환
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 작품 관련 예외 (작품 없음, 권한 없음, 학생 아님)
    @ExceptionHandler(ArtworkException.class)
    public ResponseEntity<ErrorResponse> handleArtworkException(ArtworkException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    // 댓글 관련 예외 (댓글 없음, 권한 없음)
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> handleCommentException(CommentException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    // 학과 관련 예외 (학과 없음, 관리자 아님)
    @ExceptionHandler(DepartmentException.class)
    public ResponseEntity<ErrorResponse> handleDepartmentException(DepartmentException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    // 사용자 관련 예외 (사용자 없음, 토큰 만료/무효, 헤더 누락)
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }

    // 전시 관련 예외 (전시 없음)
    @ExceptionHandler(ExhibitionException.class)
    public ResponseEntity<ErrorResponse> handleExhibitionException(ExhibitionException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getMessage()));
    }
}
