package comso.Team5.GP.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserArtworkDetailResponseDto {
    private Long userId;
    private String nickname;
    private String profileImage;
}
