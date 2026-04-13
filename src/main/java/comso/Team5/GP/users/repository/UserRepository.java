package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByLoginId(String loginId);

    ///  이메일 중복 체크
    boolean existsById(String id);
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);

}
