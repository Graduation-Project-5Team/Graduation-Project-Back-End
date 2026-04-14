package comso.Team5.GP.users.controller;

import comso.Team5.GP.users.dto.request.EmailSendRequestDto;
import comso.Team5.GP.users.dto.request.EmailVerifyRequestDto;
import comso.Team5.GP.users.dto.request.SignupRequestDto;
import comso.Team5.GP.users.service.EmailService;
import comso.Team5.GP.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

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
}