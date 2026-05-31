package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    // 유저 정보 조회 시 아이디로 유저 조회(비밀번호 찾기 등)
    @Query("SELECT u FROM Users u WHERE u.id = :id")
    Optional<Users> findByCheckId(@Param("id") String id);

    // 로그인 시 아이디로 유저 조회
    @Query("SELECT u FROM Users u WHERE u.id = :id")
    Optional<Users> findByUserLoginId(@Param("id") String id);

    // 회원가입 시 아이디 중복 체크
    @Query("SELECT COUNT(u) > 0 FROM Users u WHERE u.id = :id")
    boolean existsByUserLoginId(@Param("id") String id);

    ///  이메일 중복 체크
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.departments")
    Page<Users> findAllAndWithDepartments(Pageable pageable);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.departments WHERE u.nickname LIKE %:keyword% OR u.id LIKE %:keyword%")
    Page<Users> findByNickNameContaining(String keyword, Pageable pageable);

    Optional<Users> findByEmail(String email); // 임시, 다른 기능(비밀번호 찾기 등)을 만들 때 필요할 예정

}
