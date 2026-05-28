package comso.Team5.GP.users.dto.response;

import comso.Team5.GP.users.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRoleChangeResponse {
    private Role role;
}
