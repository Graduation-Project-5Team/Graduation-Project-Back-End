package comso.Team5.GP.users.service;

import comso.Team5.GP.artworks.dto.response.ArtworkHidingResponse;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final ArtworkRepository artworkRepository;

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
