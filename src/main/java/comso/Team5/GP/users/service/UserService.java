package comso.Team5.GP.users.service;

import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.UserMePasswordUpdateRequest;
import comso.Team5.GP.users.dto.response.UserLoginResponse;
import comso.Team5.GP.users.dto.response.UserMeNicknameUpdateResponse;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.dto.request.UserLoginRequest;
import comso.Team5.GP.users.dto.response.UserMeResponse;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import comso.Team5.GP.util.jwt.JwtUtil;
import comso.Team5.GP.users.dto.request.SignupRequestDto;
import comso.Team5.GP.users.repository.EmailVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService{

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailVerificationRepository emailVerificationRepository; // 이메일 인증 여부 확인용
    private final RefreshTokenService refreshTokenService; // RefreshTokenService 주입

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
        Users user = userRepository.findByCheckId(loginRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "아이디 혹은 비밀번호가 올바르지 않습니다."));

        // 유저 비밀번호 검증
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "아이디 혹은 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtUtil.generateAccess(user.getUserId(), user.getId());
        String refreshToken = jwtUtil.generateRefresh(user.getUserId(), user.getId());

        // 메서드를 통해 발급한 리프레시 토큰을 db에 저장
        user.updatedRefreshToken(refreshToken);

        return new UserLoginResponse(accessToken, "Bearer", jwtUtil.getExpirationSeconds(), refreshToken, "Bearer", jwtUtil.getRefreshExpirationsSeconds());
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
    public void logout(Long userId) { // String id -> Long userId

        // 유저 정보 조회
        Users user = userRepository.findById(userId) // findByCheckId -> findById
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        // 리프레시 토큰 무효화 (RefreshTokenService 사용)
        refreshTokenService.deleteByUser(user);
        log.info("RefreshToken 무효화 성공 (UserService logout)");
    }

    // 유저 프로필 조회
    public UserMeResponse getUserMe(Long userId) { // String id -> Long userId

        Users user = userRepository.findById(userId).orElseThrow( // findByCheckId -> findById
                () -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        return new UserMeResponse(user.getId(), user.getNickname(), user.getRole().name(), user.getEmail());
    }

    // 유저 닉네임 변경
    @Transactional
    public UserMeNicknameUpdateResponse updateNickname(Long userId, String nickname) {

        // UserId로 정보 조회 (존재하지 않을 경우 예외처리)
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        // 존재하는 경우 바꿀려고 하는 닉네임으로 변경
        user.updateUserNickname(nickname);

        // 해당 닉네임만 담겨져 있는 dto 객체로 반환
        return new UserMeNicknameUpdateResponse(user.getNickname());
    }

    @Transactional
    public void updatePasswordMe(Long userId, UserMePasswordUpdateRequest request) {

        // 이메일 인증 완료 여부 체크
        if (!emailVerificationRepository.existsByEmailAndIsVerifiedTrue(request.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "이메일 인증이 완료되지 않았습니다.");
        }

        // 유저 아이디를 통해 정보 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        // 유저 패스워드 변경
        user.updateUserPassword(request.getPassword());
    }

}
