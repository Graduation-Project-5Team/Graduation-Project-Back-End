package comso.Team5.GP.users.service;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.dto.request.UserLoginRequest;
import comso.Team5.GP.users.dto.response.UserMeResponse;
import comso.Team5.GP.users.dto.response.UserLoginResponse;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import comso.Team5.GP.util.jwt.JwtUtil;
import comso.Team5.GP.users.dto.request.SignupRequestDto;
import comso.Team5.GP.users.repository.EmailVerificationRepository;
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
    private final EmailVerificationRepository emailVerificationRepository; // 이메일 인증 여부 확인용

    // 학생 이메일 도메인 (상수)
    private static final String STUDENT_EMAIL_DOMAIN = "@gsuite.induk.ac.kr";


    //  로그인
    @Transactional
    public UserLoginResponse login(UserLoginRequest loginRequest){

        // 유저 로그인 정보 검증
        if (loginRequest.getId() == null || loginRequest.getId().isBlank() ||
        loginRequest.getPassword() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "user_id 혹은 비밀번호가 불일치 합니다.");
        }

        // 유저 로그인 정보 조회
        Users users = userRepository.findByCheckId(loginRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "아이디 혹은 비밀번호가 올바르지 않습니다."));

        // 유저 비밀번호 검증
        if (!loginRequest.getPassword().equals(users.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "아이디 혹은 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtUtil.generateAccess(users.getUserId(), users.getId());
        String refreshToken = jwtUtil.generateRefresh(users.getUserId());

        // 메서드를 통해 발급한 리프레시 토큰을 db에 저장
        users.updatedRefreshToken(refreshToken);

        return new UserLoginResponse(accessToken, refreshToken);
    }


    /// 회원가입
    @Transactional
    public void signup(SignupRequestDto dto) {

        // 중복 아이디 체크
        if (userRepository.existsByUserLoginId(dto.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "이미 사용 중인 아이디입니다.");
        }

        // 중복 이메일 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "이미 가입된 이메일입니다.");
        }

        // 이메일 인증 완료 여부 체크
        if (!emailVerificationRepository.existsByEmailAndIsVerifiedTrue(dto.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "이메일 인증이 완료되지 않았습니다.");
        }

        // 학생 여부 판단
        Role role = dto.getEmail().endsWith(STUDENT_EMAIL_DOMAIN)
                ? Role.STUDENT
                : Role.USER;

        // [MVP] 비밀번호 암호화 없이 그대로 저장 (나중에 PasswordEncoder 추가 필요)
        Users user = Users.builder()
                .id(dto.getId())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .nickname(dto.getName())
                .role(role)
                .isVerified(true)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void logout(Long userId) {

        // 유저 정보 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        // 리프레시 토큰 무효화
        user.updatedRefreshToken(null);
    }


    public UserMeResponse getUserMe(Long userId, String id) {

        Users user = userRepository.findByCheckId(id).orElseThrow(
                () -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        return new UserMeResponse(user.getId(), user.getNickname(), user.getRole().name(), user.getEmail());
    }
}
