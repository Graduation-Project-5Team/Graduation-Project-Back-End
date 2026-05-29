package comso.Team5.GP.comments.dto.response;

import comso.Team5.GP.comments.entity.CommentDeletedBy;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDeletedByResponse {
    private CommentDeletedBy deletedBy;
}
