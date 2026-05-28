package comso.Team5.GP.users.service;

import comso.Team5.GP.artworks.dto.response.ArtworkHidingResponse;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.UserRoleChangeRequest;
import comso.Team5.GP.users.dto.response.UserListResponse;
import comso.Team5.GP.users.dto.response.UserRoleChangeResponse;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;

    @Transactional
    public Page<UserListResponse> getUserList(Role role, String keyword, Pageable pageable) {

        if (!role.equals(Role.ADMIN)) {
            throw new UserException(UserExceptionCode.NOT_ADMIN);
        }

        Page<Users> users;

        if(keyword == null || keyword.isBlank()) {
            users = userRepository.findAllAndWithDepartments(pageable);
        } else {
            users = userRepository.findByNickNameContaining(keyword, pageable);
        }

        return users.map(UserListResponse::from);
    }

    @Transactional
    public UserRoleChangeResponse changeRole(JwtPrincipal principal, UserRoleChangeRequest request) {

        // 관리자가
        if (principal.userId().equals(request.getUserId())) {
            throw new UserException(UserExceptionCode.CANNOT_CHANGE_SELF_ROLE);
        }

        if (!principal.role().equals(Role.ADMIN)) {
            throw new UserException(UserExceptionCode.NOT_ADMIN);
        }

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        user.updateUserRole(request.getRole());

        return new UserRoleChangeResponse(user.getRole());
    }

    @Transactional
    public ArtworkHidingResponse artworkHiding(Role role, Long artworkId) {

        // 관리자 권한이 아닐 시 예외처리
        if (role != Role.ADMIN) {
            throw new UserException(UserExceptionCode.NOT_ADMIN);
        }

        // 숨김 처리할 작품 조회
        Artworks artwork = artworkRepository.findById(artworkId).
                orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        // 기존에 작품이 공개/숨김 처리인지 확인 후 반대의 결과값으로 db에 저장
        if (artwork.isHidden()) {
            artwork.updateHidden(false);
        } else {
            artwork.updateHidden(true);
        }

        String message = artwork.isHidden() ? "숨김 처리가 완료되었습니다." : "숨김 처리가 해제되었습니다.";

        return new ArtworkHidingResponse(message, artwork.isHidden());
    }
}
