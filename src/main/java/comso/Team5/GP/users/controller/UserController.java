package comso.Team5.GP.users.controller;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.TokenReissueRequest;
import comso.Team5.GP.users.dto.request.UserLoginRequest;
import comso.Team5.GP.users.dto.response.TokenReissueRefreshResponse;
import comso.Team5.GP.users.dto.response.UserLoginResponse;
import comso.Team5.GP.users.dto.response.UserMeResponse;
import comso.Team5.GP.users.service.RefreshTokenService;
import comso.Team5.GP.users.service.UserService;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users") // 공통 경로 /api/auth로 변경
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> userLogin(@RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueRefreshResponse> reissueToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        String token = authHeader.substring(7);
        TokenReissueRequest reissueRequest = new TokenReissueRequest(token);

        TokenReissueRefreshResponse response = refreshTokenService.reissueTokens(reissueRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getUserMe(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }
        String token = authHeader.substring(7);
        Long userId = jwtUtil.getPrincipalFromToken(token).userId();
        UserMeResponse response = userService.getUserMe(userId); // userId 전달
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }
        String token = authHeader.substring(7);
        Long userId = jwtUtil.getPrincipalFromToken(token).userId();
        userService.logout(userId); // userId 전달
        return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
    }
}
