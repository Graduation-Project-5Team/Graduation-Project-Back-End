package comso.Team5.GP.comments.dto.response;


import comso.Team5.GP.comments.entity.CommentDeletedBy;
import comso.Team5.GP.comments.entity.Comments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CommentListResponse {
    private Long commentId;

    private CommentUserResponseDto user;

    private Long artworkId;

    private String content;

    private LocalDateTime createdAt;

    private boolean deleted;

    private CommentDeletedBy deletedBy;

    private LocalDateTime deletedAt;

    public static CommentListResponse from(Comments comments) {
        return CommentListResponse.builder()
                .commentId(comments.getCommentId())
                .user(new CommentUserResponseDto(comments.getUser().getUserId(), comments.getUser().getNickname(), comments.getUser().getProfileImage()))
                .artworkId(comments.getArtwork().getArtworkId())
                .content(comments.getContent())
                .createdAt(comments.getCreatedAt())
                .deleted(comments.isDeleted())
                .deletedBy(comments.getDeletedBy())
                .deletedAt(comments.getDeletedAt())
                .build();
    }
}
