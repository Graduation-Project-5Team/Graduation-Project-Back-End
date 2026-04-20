package comso.Team5.GP.users.entity;

import comso.Team5.GP.departments.entity.Departments;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder // 객체 생성 시 Users.builder().id("...").email("...").build() 형태로
//        원하는 필드만 골라서 가독성 좋게 생성할 수 있게 해줌 (SignupService에서 사용)
@AllArgsConstructor // @Builder가 내부적으로 전체 필드 생성자를 필요로 하기 때문에 추가.
//        @NoArgsConstructor와 @Builder를 같이 쓰면 컴파일 에러가 발생
@NoArgsConstructor
@Table(name = "users")
public class Users {

    // 데이터 생성시 유저 테이블 id 자동 증가
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "id", nullable = false, unique = true) // 같은 아이디 중복 가입 방지를 위한 unique 제약조건 추가
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true) // 같은 이메일 중복 가입 방지를 위한 unique 제약조건 추가
    private String email;

    @Column
    private String nickname;

    @Column
    private String role;

    // 학과 테이블과 조인 (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", referencedColumnName = "dept_id")
    private Departments departments;

    @Column(name = "is_verified")
    private boolean isVerified;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false) // 생성 시점 자동 기록, 이후 수정 불가
    private LocalDateTime createAt;


    // @Setter 제거(직접 구현) -> 이메일 인증 완료 시 호출하는 메서드
    public void markAsVerified() {
        this.isVerified = true;
    }

}