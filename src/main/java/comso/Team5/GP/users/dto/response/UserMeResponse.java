package comso.Team5.GP.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMeResponse {

    private String id;
    
    private String nickname;

    private String role;
    
    private String email;

    private Long departmentId;

    private String departmentName;
}
