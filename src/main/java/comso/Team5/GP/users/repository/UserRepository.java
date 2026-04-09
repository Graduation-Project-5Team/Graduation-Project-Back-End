package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
