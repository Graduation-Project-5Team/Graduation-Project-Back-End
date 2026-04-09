package comso.Team5.GP.users.repository;

import comso.Team5.GP.users.entity.Users;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("user 생성 테스트")
    @Transactional
    void saveUser() {
        Users user = new Users();
        user.setUserId("test123");
        user.setEmail("test123@test.123");
        user.setPassword("test123");
        user.setNickname("test123");
        user.setRole("USER");
        user.setVerified(false);
        user.setDeptId(1);

        Users saveUser = userRepository.save(user);

        assertThat(saveUser.getId()).isGreaterThan(0L);
        assertThat(saveUser.getUserId()).isEqualTo("test123");
    }
}
