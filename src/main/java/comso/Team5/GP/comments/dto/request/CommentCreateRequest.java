package comso.Team5.GP.comments.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private Long userId;

    private Long artworkId;

    private String content;
}
