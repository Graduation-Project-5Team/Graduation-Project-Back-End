package comso.Team5.GP.users.service;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.response.TokenReissueRefreshResponse;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import comso.Team5.GP.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public void deleteByUser(Users user) {
        user.updatedRefreshToken(null);
        userRepository.save(user);
    }

    @Transactional
    public TokenReissueRefreshResponse reissueTokens(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사
        jwtUtil.validate(refreshToken);

        // 2. 토큰에서 userId 추출
        Long userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);

        // 3. DB에서 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        // 4. DB의 리프레시 토큰과 일치하는지 확인 (보안 강화)
        if (!Objects.equals(user.getRefreshToken(), refreshToken)) {
            // 토큰이 일치하지 않는 경우, 탈취 가능성을 고려하여 DB의 토큰을 무효화
            deleteByUser(user);
            throw new SecurityException("Invalid refresh token.");
        }

        // 5. 새로운 토큰 생성 (토큰 로테이션)
        String newAccessToken = jwtUtil.generateAccess(user.getUserId(), user.getId(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefresh(user.getUserId(), user.getId(), user.getRole());

        // 6. DB에 새로운 리프레시 토큰 저장
        user.updatedRefreshToken(newRefreshToken);
        userRepository.save(user);

        // 7. 새로운 토큰 반환
        return new TokenReissueRefreshResponse(
                newAccessToken,
                "Bearer",
                jwtUtil.getExpirationSeconds(),
                newRefreshToken,
                "Bearer",
                jwtUtil.getRefreshExpirationsSeconds()
        );
    }
}
