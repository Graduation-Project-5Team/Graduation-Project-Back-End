package comso.Team5.GP.util.jwt;

// JwtUtil에 Principal 생성자를 분리
public record JwtPrincipal(
        Long userId,
        String id) {
}
