package comso.Team5.GP.user.repository;

import comso.Team5.GP.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
