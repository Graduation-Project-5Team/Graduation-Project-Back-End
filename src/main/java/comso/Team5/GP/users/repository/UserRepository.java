package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findById(String id);


    // 로그인 시 아이디로 유저 조회
    @Query("SELECT u FROM Users u WHERE u.id = :id")
    Optional<Users> findByUserLoginId(@Param("id") String id);

    // 회원가입 시 아이디 중복 체크
    @Query("SELECT COUNT(u) > 0 FROM Users u WHERE u.id = :id")
    boolean existsByUserLoginId(@Param("id") String id);

    ///  이메일 중복 체크
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);

}
