package comso.Team5.GP.util.jwt;

import comso.Team5.GP.users.entity.Role;

// JwtUtil에 Principal 생성자를 분리
public record JwtPrincipal(
        Long userId,
        String id,
        Role role) {
}
