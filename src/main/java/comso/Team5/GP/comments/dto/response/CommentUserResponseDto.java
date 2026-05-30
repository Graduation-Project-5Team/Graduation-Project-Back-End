package comso.Team5.GP.comments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentUserResponseDto {
    private Long userId;

    private String nickname;
}
