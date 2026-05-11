package comso.Team5.GP.artworks.service;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.request.ArtworkUpdateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkResponse;
import comso.Team5.GP.artworks.entity.ArtworkImages;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
public class ArtworkService {

    @Value("${file.upload-dir}")
    private String uploadDir;

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
    public ArtworkCreateResponse create(ArtworkCreateRequest request, Long userId, List<MultipartFile> files) {

        // 전시 존재 여부 확인
        Exhibitions exhibitions = exhibitionRepository.findById(request.getExhiId())
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        // 유저 확인
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        // 학생 인증 확인
        if (user.getRole() != Role.STUDENT && user.getRole() != Role.ADMIN) {
            throw new ArtworkException(ArtworkExceptionCode.NOT_STUDENT);
        }

        // /uploads가 존재하지 않을 경우 디렉터리 생성
        File dir = new File((uploadDir));
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<ArtworkImages> artworkImages = saveImages(files, dir);

        Artworks artwork = Artworks.builder()
                .exhibitions(exhibitions)
                .users(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // artworkImage와 연관 관계를 맺기 위한 메서드
        artwork.setImages(artworkImages);

        Artworks save = artworkRepository.save(artwork);

        return new ArtworkCreateResponse(save.getArtworkId());
    }

    // 작품 수정 (본인만 가능)
    @Transactional
    public ArtworkResponse update(Long artworkId,
                                  ArtworkUpdateRequest request,
                                  Long userId,
                                  List<MultipartFile> images) {
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        if (!artwork.getUsers().getUserId().equals(userId)) {
            throw new ArtworkException(ArtworkExceptionCode.FORBIDDEN_ARTWORK);
        }

        // request 값이 존재하면
        if (request != null) {
            if (request.getTitle() != null) artwork.setTitle(request.getTitle());
            if (request.getDescription() != null) artwork.setDescription(request.getDescription());
        }

        // 바꾼 사진이 존재하면
        if (images != null && !images.isEmpty()) {

            // 새 이미지 저장
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            List<ArtworkImages> newImages = saveImages(images, dir);

            // DB에서 이미지 업데이트 (cascade와 orphanRemoval에 의해 처리됨)
            artwork.setImages(newImages);

        }
        artwork.setUpdatedAt(LocalDateTime.now());

        Artworks updatedArtwork = artworkRepository.save(artwork);
        return toResponse(updatedArtwork);
    }

    // 작품 삭제 (본인만 가능)
    public void delete(Long artworkId, Long userId) {
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        if (!artwork.getUsers().getUserId().equals(userId)) {
            throw new ArtworkException(ArtworkExceptionCode.FORBIDDEN_ARTWORK);
        }
        
        // 물리적 파일 삭제
        deletePhysicalFiles(new ArrayList<>(artwork.getImageUrl()));

        artworkRepository.delete(artwork);
    }

    private ArtworkResponse toResponse(Artworks artwork) {
        List<String> imageUrls = artwork.getImageUrl()
                .stream().map(ArtworkImages::getImageUrl)
                .collect(Collectors.toList());

        return new ArtworkResponse(
                artwork.getArtworkId(),
                artwork.getUsers().getUserId(),
                artwork.getExhibitions() != null ? artwork.getExhibitions().getExhiId() : null,
                artwork.getTitle(),
                artwork.getDescription(),
                imageUrls,
                artwork.getLikeCount(),
                artwork.getCreatedAt(),
                artwork.getUpdatedAt()
        );
    }

    // 물리적 이미지 파일 삭제 메서드
    private void deletePhysicalFiles(List<ArtworkImages> files) {
        if (files == null || files.isEmpty()) return;

        for (ArtworkImages image : files) {
            if (image.getImageUrl() == null || image.getImageUrl().isEmpty()) continue;
            try {
                String fileName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
                File fileToDelete = new File(uploadDir, fileName);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            } catch (Exception e) {
                System.err.println("Failed to delete image file: " + image.getImageUrl() + " - " + e.getMessage());
            }
        }
    }

    private List<ArtworkImages> saveImages(List<MultipartFile> files, File dir) {
        // 작품 엔티티에 저장하기 전 작품의 이미지들을 저장할 리스트
        List<ArtworkImages> artworkImages = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                try {
                    String originalFileName = file.getOriginalFilename();
                    String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

                    // File(parent, child) 생성자를 사용하여 파일 객체 생성
                    File dest = new File(dir, savedFileName);
                    file.transferTo(dest);

                    ArtworkImages image = ArtworkImages.builder()
                            .imageUrl(savedFileName)
                            .build();

                    artworkImages.add(image);
                } catch (IOException e) {
                    throw new RuntimeException("작품 이미지 저장 중 오류가 발생 했습니다.", e);
                }
            }
        }
        return artworkImages;
    }
}
