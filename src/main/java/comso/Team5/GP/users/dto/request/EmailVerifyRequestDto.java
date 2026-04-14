package comso.Team5.GP.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EmailVerifyRequestDto { // 이메일 인증 요청 검증

    @NotBlank @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 6, message = "인증코드는 6자리입니다.")
    private String code;
}