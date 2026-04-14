package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 가장 최근에 발송된 인증 정보 조회
    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);

    // 인증 완료된 이메일인지 확인
    boolean existsByEmailAndIsVerifiedTrue(String email);
}