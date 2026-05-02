package comso.Team5.GP.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenReissueRefreshResponse {
    private String accessToken;

    private String tokenType;

    private long expiresInSeconds;

    private String refreshToken;

    private String refreshTokenType;

    private long refreshExpiresInSecends;
}
