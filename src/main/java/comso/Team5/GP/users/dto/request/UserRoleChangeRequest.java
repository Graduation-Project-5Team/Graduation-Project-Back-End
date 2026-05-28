package comso.Team5.GP.users.dto.request;

import comso.Team5.GP.users.entity.Role;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserRoleChangeRequest {

    @NonNull
    private Long userId;

    @NonNull
    private Role role;
}
