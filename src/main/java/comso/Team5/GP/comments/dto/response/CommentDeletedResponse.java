package comso.Team5.GP.comments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDeletedResponse {
    private String message;

    private boolean deleted;
}
