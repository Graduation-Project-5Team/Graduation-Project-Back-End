package comso.Team5.GP.users.dto.response;

import lombok.Getter;

@Getter
public class UserIdResponse {

    private Long userId;

    public UserIdResponse(Long userId) {
        this.userId = userId;
    }
}
