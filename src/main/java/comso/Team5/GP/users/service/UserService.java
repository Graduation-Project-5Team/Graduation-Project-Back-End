package comso.Team5.GP.users.service;

import comso.Team5.GP.users.dto.UserLoginRequest;
import comso.Team5.GP.users.dto.UserLoginResponse;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import comso.Team5.GP.util.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService{

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserLoginResponse login(UserLoginRequest loginRequest){

        // 유저 로그인 정보 검증
        if (loginRequest.getId() == null || loginRequest.getId().isBlank() ||
        loginRequest.getPassword() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "user_id 혹은 비밀번호가 불일치 합니다.");
        }

        // 유저 로그인 정보 조회
        Users users = userRepository.findByLoginId(loginRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "아이디 혹은 비밀번호가 올바르지 않습니다."));

        // 유저 비밀번호 검증
        if (!loginRequest.getPassword().equals(users.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "아이디 혹은 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtUtil.generateToken(users.getUserId(), users.getLoginId());

        return new UserLoginResponse(accessToken, "Bearer", jwtUtil.getExpirationSeconds());
    }

}
