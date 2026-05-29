package comso.Team5.GP.comments.dto.response;

import comso.Team5.GP.comments.entity.CommentDeletedBy;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;

    private CommentUserResponseDto user;

    private Long artworkId;

    private String content;

    private LocalDateTime createdAt;

    private boolean deleted;

    private CommentDeletedBy deletedBy;

    private LocalDateTime deletedAt;
}
