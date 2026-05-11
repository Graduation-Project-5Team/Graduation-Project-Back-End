package comso.Team5.GP.users.service;

import comso.Team5.GP.global.exception.auth.AuthException;
import comso.Team5.GP.global.exception.auth.AuthExceptionCode;
import comso.Team5.GP.users.entity.EmailVerification;
import comso.Team5.GP.users.repository.EmailVerificationRepository;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Slf4j // 로그(Log)를 쉽고 편하게 남길 수 있도록 도와주는 Lombok(롬복) 라이브러리의 어노테이션
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository; // 이메일 중복 체크용

    // 인증코드 유효시간 5분
    private static final int CODE_EXPIRY_MINUTES = 5;

    // 인증코드 발송
    @Transactional
    public void sendVerificationCode(String email) {

        // 이미 가입된 이메일이면 인증코드 발송 차단
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(BAD_REQUEST, "이미 가입된 이메일입니다.");
        }

        // 신규 사용자 이메일 인증코드 발송
        issueAndSendCode(email);
    }

    // 인증코드 검증
    @Transactional
    public void verifyCode(String email, String code) {

        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 정보가 없습니다. 인증코드를 다시 요청해주세요."));

        // 만료 시간 체크
        if (verification.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("인증코드가 만료되었습니다. 다시 요청해주세요.");
        }

        // 코드 일치 여부 체크
        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증코드가 올바르지 않습니다.");
        }

        verification.verify(); // isVerified = true
    }

    // 6자리 랜덤 숫자 코드 생성
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // 이메일 발송
    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[GP] 이메일 인증 코드");
        message.setText(
                "안녕하세요, GP 서비스입니다.\n\n" +
                        "이메일 인증 코드: " + code + "\n\n" +
                        "유효시간: " + CODE_EXPIRY_MINUTES + "분\n" +
                        "본인이 요청하지 않은 경우 이 메일을 무시해주세요."
        );
        mailSender.send(message);
    }

    // 이메일 인증 요청 메서드
    private void issueAndSendCode(String email) {
        String code = generateCode();

        // 기존 인증 요청이 있으면 삭제 후 새로 저장 (재발송 처리)
        emailVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .ifPresent(emailVerificationRepository::delete);

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .isVerified(false)
                .expiredAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES))
                .build();

        emailVerificationRepository.save(verification);
        sendEmail(email, code);

        log.info("인증코드 발송 완료 - 이메일: {}", email); // @Slf4j 사용
    }

    // 기존 사용자 이메일 인증
    public void updateSendEmail(String email) {
        // 사용자가 존재하는지 먼저 확인
        userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthExceptionCode.NOT_FOUND_EMAIL));

        // 기존 사용자 이메일 인증코드 발송
        issueAndSendCode(email);
    }
}