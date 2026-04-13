package comso.Team5.GP.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private String accessToken;

    private String tokenType;

    private long expiresInSeconds;

}
