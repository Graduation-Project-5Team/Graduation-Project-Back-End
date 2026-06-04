package comso.Team5.GP.users.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserDepartmentsChangeRequest {

    private Long userId;

    @NotNull(message = "학과를 선택해주세요.")
    private Long deptId;
}
