package comso.Team5.GP.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueRequest {
    private String refreshToken;
}
