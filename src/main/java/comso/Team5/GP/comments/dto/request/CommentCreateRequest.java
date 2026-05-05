package comso.Team5.GP.comments.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequest {

    private Long artworkId;

    private String content;
}
