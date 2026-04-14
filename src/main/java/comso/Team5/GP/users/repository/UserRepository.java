package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findById(String id);

    ///  이메일 중복 체크
    //        JpaRepository의 existsById(Long id)와 충돌 방지
    boolean existsByIdField(String id); // id 컬럼 중복 체크
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);

}
