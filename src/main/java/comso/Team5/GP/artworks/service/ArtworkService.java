package comso.Team5.GP.artworks.service;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.request.ArtworkUpdateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkResponse;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.exception.UserException;
import comso.Team5.GP.users.exception.UserExceptionCode;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;

    // 작품 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<ArtworkResponse> getArtworks(Pageable pageable) {

        return artworkRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // 작품 단건 조회
    @Transactional(readOnly = true)
    public ArtworkResponse getArtwork(Long artworkId) {

        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 작품이 존재하지 않습니다. 작품 ID : " + artworkId));

        return toResponse(artwork);
    }

    // 작품 등록 (JWT에서 추출한 userId로 작성자 설정)
    @Transactional
    public ArtworkCreateResponse create(ArtworkCreateRequest request, Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Artworks artwork = Artworks.builder()
                .users(user)
                .title(request.getTitle())
                .content(request.getContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Artworks save = artworkRepository.save(artwork);

        return new ArtworkCreateResponse(save.getArtworkId());
    }

    // 작품 수정 (본인 확인 후 수정)
    @Transactional
    public ArtworkResponse update(Long artworkId, ArtworkUpdateRequest request, Long userId) {

        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 작품이 존재하지 않습니다. 작품 ID : " + artworkId));

        // 작품 작성자와 요청자가 일치하는지 확인
        if (!artwork.getUsers().getUserId().equals(userId)) {
            throw new UserException(UserExceptionCode.INVALID_TOKEN);
        }

        artwork.setTitle(request.getTitle());
        artwork.setContent(request.getContent());
        artwork.setStartDate(request.getStartDate());
        artwork.setEndDate(request.getEndDate());
        artwork.setUpdatedAt(LocalDateTime.now());

        return toResponse(artwork);
    }

    // 작품 삭제 (본인 확인 후 삭제)
    @Transactional
    public void delete(Long artworkId, Long userId) {

        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 작품이 존재하지 않습니다. 작품 ID : " + artworkId));

        // 작품 작성자와 요청자가 일치하는지 확인
        if (!artwork.getUsers().getUserId().equals(userId)) {
            throw new UserException(UserExceptionCode.INVALID_TOKEN);
        }

        artworkRepository.delete(artwork);
    }

    // Artworks 엔티티를 ArtworkResponse DTO로 변환
    private ArtworkResponse toResponse(Artworks artwork) {
        return new ArtworkResponse(
                artwork.getArtworkId(),
                artwork.getUsers().getUserId(),
                artwork.getExhibitions() != null ? artwork.getExhibitions().getExhiId() : null,
                artwork.getTitle(),
                artwork.getContent(),
                artwork.getLikeCount(),
                artwork.getStartDate(),
                artwork.getEndDate(),
                artwork.getCreatedAt(),
                artwork.getUpdatedAt()
        );
    }
}
