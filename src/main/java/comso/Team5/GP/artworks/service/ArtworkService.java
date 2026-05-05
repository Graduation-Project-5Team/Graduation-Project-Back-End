package comso.Team5.GP.artworks.service;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.request.ArtworkUpdateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkResponse;
import comso.Team5.GP.users.entity.Role;
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

        if (user.getRole() != Role.STUDENT) {
            throw new ArtworkException(ArtworkExceptionCode.NOT_STUDENT);
        }

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

    // 작품 수정 (본인만 가능)
    public ArtworkResponse update(Long artworkId, ArtworkUpdateRequest request, Long userId) {
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        if (!artwork.getUsers().getUserId().equals(userId)) {
            throw new ArtworkException(ArtworkExceptionCode.FORBIDDEN_ARTWORK);
        }

        if (request.getTitle() != null) artwork.setTitle(request.getTitle());
        if (request.getDescription() != null) artwork.setDescription(request.getDescription());
        artwork.setUpdatedAt(LocalDateTime.now());

        return toResponse(artwork);
    }

    // 작품 삭제 (본인만 가능)
    public void delete(Long artworkId, Long userId) {
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        if (!artwork.getUsers().getUserId().equals(userId)) {
            throw new ArtworkException(ArtworkExceptionCode.FORBIDDEN_ARTWORK);
        }

        artworkRepository.delete(artwork);
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
