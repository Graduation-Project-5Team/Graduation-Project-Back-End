package comso.Team5.GP.users.controller;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.*;
import comso.Team5.GP.users.dto.response.*;
import comso.Team5.GP.users.service.RefreshTokenService;
import comso.Team5.GP.users.service.UserService;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.util.Map;

@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @PostMapping("/student-signup")
    public ResponseEntity<Map<String, String>> studentSignup(@Valid @RequestBody SutdentSignupRequestDto dto) {
        userService.studentSignup(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
    }

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

        Long userId = extractPrincipal(request).userId();

        UserMeResponse response = userService.getUserMe(userId); // userId 전달

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(HttpServletRequest request) {

        Long userId = extractPrincipal(request).userId();

        userService.logout(userId); // userId 전달

        return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
    }

    // 유저 닉네임 변경
    @PatchMapping("/me/nickname")
    public ResponseEntity<UserMeNicknameUpdateResponse> userMeUpNicknameUpdate(@RequestBody UserMeNicknameUpdateRequest dto,
                                                                               HttpServletRequest request) {

        Long userId = extractPrincipal(request).userId();


        // RequestDto(이메일) 값이 존재하지 않을 경우
        if (dto == null || dto.getNickname() == null) {
            throw new UserException(UserExceptionCode.USER_INFO_NICKNAME_NOT_FOUND);
        }

        // 존재하는 경우 서비스 계층에서 로직 수행
        return ResponseEntity.ok(userService.updateNickname(userId, dto.getNickname()));
    }

    // 유저 프로필 이미지 추가
    @PatchMapping("/me/profile-image")
    public ResponseEntity<UserUpdateProfileImageResponse> addProfileImage(
            @RequestPart("image") MultipartFile image,
            HttpServletRequest request) {

        Long userId = extractPrincipal(request).userId();

        UserUpdateProfileImageResponse response = userService.addProfileImage(image, userId);

        return ResponseEntity.ok(response);
    }

    // 학생 인증 업데이트
    @PatchMapping("/me/isVerified")
    public ResponseEntity<Map<String, String>> isVerifiedAndEmailUpdate(@Valid @RequestBody UserStudentIsVerifiedUpdateRequest dto,
                                                                     HttpServletRequest request) {
        long userId = extractPrincipal(request).userId();

        userService.isVerifiedAndEmailUpdate(userId, dto);

        return ResponseEntity.ok(Map.of("message", "학생 인증이 완료되었습니다."));
    }

    private JwtPrincipal extractPrincipal(HttpServletRequest request) {

        // 사용자 토큰을 이용해 권한 확인
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        // 토큰 추출 및 유저ID 추출
        String token = authHeader.substring(7);

        return jwtUtil.getPrincipalFromToken(token);
    }
}
