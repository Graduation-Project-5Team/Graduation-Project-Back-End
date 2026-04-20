package comso.Team5.GP.users.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserLoginRequest {

    @JsonProperty("id")
    private String id;

    private String password;
}
