package comso.Team5.GP.artworks.service;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkResponse;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.exhibitions.repository.ExhibitionRepository;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionException;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;

    // 작품 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<ArtworkResponse> getArtworks(Pageable pageable) {
        return artworkRepository.findAll(pageable).map(this::toResponse);
    }

    // 작품 단건 조회
    @Transactional(readOnly = true)
    public ArtworkResponse getArtwork(Long artworkId) {
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));
        return toResponse(artwork);
    }

    // 작품 등록
    @Transactional
    public ArtworkCreateResponse create(ArtworkCreateRequest request, Long userId) {

        Exhibitions exhibitions = exhibitionRepository.findById(request.getExhiId())
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        Artworks artwork = Artworks.builder()
                .exhibitions(exhibitions)
                .users(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Artworks save = artworkRepository.save(artwork);

        return new ArtworkCreateResponse(save.getArtworkId());
    }

    private ArtworkResponse toResponse(Artworks artwork) {
        return new ArtworkResponse(
                artwork.getArtworkId(),
                artwork.getUsers().getUserId(),
                artwork.getExhibitions() != null ? artwork.getExhibitions().getExhiId() : null,
                artwork.getTitle(),
                artwork.getDescription(),
                artwork.getLikeCount(),
                artwork.getCreatedAt(),
                artwork.getUpdatedAt()
        );
    }
}
