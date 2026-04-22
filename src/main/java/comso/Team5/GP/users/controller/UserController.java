package comso.Team5.GP.users.controller;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.UserLoginRequest;
import comso.Team5.GP.users.dto.response.UserMeResponse;
import comso.Team5.GP.users.dto.response.UserLoginResponse;
import comso.Team5.GP.users.service.UserService;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> UserLogin(@RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getUserMe(HttpServletRequest request) {

        String authUser = request.getHeader("Authorization");

        // 없거나 "Bearer " 형식이 아니면 401 (본문은 UserExceptionHandler와 동일 형식)
        if (authUser == null || !authUser.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        // 순수 토큰에 대한 내용만 저장
        String token = authUser.substring(7);

        // UserId와 id를 가져옴
        JwtUtil.JwtPrincipal principal = jwtUtil.getPrincipalFromToken(token);

        // 가져온 UserId와 id를 통해 서비스 로직 진행행
        UserMeResponse response = userService.getUserMe(principal.userId(), principal.id());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> UserLogout(HttpServletRequest request) {
        String authUser = request.getHeader("Authorization");

        if (authUser == null || !authUser.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
    }

}
