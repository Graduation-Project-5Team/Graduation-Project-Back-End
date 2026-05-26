package comso.Team5.GP.users.controller;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.*;
import comso.Team5.GP.users.service.EmailService;
import comso.Team5.GP.users.service.UserService;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/users/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @PostMapping("/api/auth/email/send")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailSendRequestDto dto) {
        emailService.sendVerificationCode(dto.getEmail());
        return ResponseEntity.ok(Map.of("message", "인증코드가 발송되었습니다. (유효시간 5분)"));
    }

    @PostMapping("/api/auth/email/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody EmailVerifyRequestDto dto) {
        emailService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/api/auth/email/update/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody UpdateEmailVerifyRequest request) {
        emailService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    // 비밀번호 변경 이메일 인증
    @PostMapping("/api/auth/email/update/send")
    public ResponseEntity<Map<String, String>> updateSendEmail(@Valid @RequestBody UpdateEmailSendRequest request) {

        emailService.updateSendEmail(request.getEmail());


        return ResponseEntity.ok(Map.of("message", "인증코드가 발송되었습니다. (유효시간 5분)"));
    }

    // 비밀번호 변경 업데이트
    @PatchMapping("/api/users/me/password")
    public ResponseEntity<Map<String, String>> userMePasswordUpdate(@Valid @RequestBody UserMePasswordUpdateRequest dto,
                                                                    HttpServletRequest request) {

        Long userId = extractPrincipal(request).userId();

        // dto 값이 존재하지 않는 경우
        if (dto == null || dto.getPassword() == null) {
            throw new UserException(UserExceptionCode.USER_INFO_PASSWORD_NOT_FOUND);
        }

        userService.updatePasswordMe(userId, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "비밀번호 변경 완료되었습니다."));
    }

    @PostMapping("/api/auth/email/me/student-send")
    public ResponseEntity<Map<String, String>> userStudentSend(@Valid @RequestBody EmailSendRequestDto dto,
                                                               HttpServletRequest request) {

        Long userId = extractPrincipal(request).userId();

        emailService.studentSend(userId, dto.getEmail());

        return ResponseEntity.ok(Map.of("message", "인증코드가 발송되었습니다. (유효시간 5분)"));
    }

    @PostMapping("/api/auth/email/me/student-verify")
    public ResponseEntity<Map<String, String>> userStudnetVerify(@Valid @RequestBody EmailVerifyRequestDto dto) {
        emailService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    private JwtPrincipal extractPrincipal(HttpServletRequest request) {
        // 리퀘스트 헤더에 권한 확인
        String authHeader = request.getHeader("Authorization");

        System.out.println("authHeader: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        // Bearer (공백 포함 7글자)
        String token = authHeader.substring(7);

        return jwtUtil.getPrincipalFromToken(token);
    }
}