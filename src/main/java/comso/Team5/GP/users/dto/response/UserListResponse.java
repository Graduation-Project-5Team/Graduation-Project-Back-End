package comso.Team5.GP.users.dto.response;

import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class UserListResponse {
    private Long userId;

    private String nickname;

    private String id;

    private String email;

    private String profileImage;

    private Role role;

    private Departments departments;

    private boolean isVerified;

    private LocalDateTime createAt;

    public static UserListResponse from(Users user) {
        return UserListResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .departments(user.getDepartments())
                .isVerified(user.isVerified())
                .createAt(user.getCreateAt())
                .build();
    }
}
