package comso.Team5.GP.comments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;

    private Long userId;

    private Long artworkId;

    private String content;

    private LocalDateTime createdAt;
}
