package comso.Team5.GP.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code; // 인증코드 6자리

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // 만료 시간

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 인증 완료 처리
    public void verify() {
        this.isVerified = true;
    }
}