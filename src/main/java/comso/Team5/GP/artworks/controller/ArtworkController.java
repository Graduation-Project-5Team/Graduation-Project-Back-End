package comso.Team5.GP.artworks.controller;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.request.ArtworkUpdateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkResponse;
import comso.Team5.GP.artworks.service.ArtworkService;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/artworks")
@RestController
public class ArtworkController {

    private final ArtworkService artworkService;
    private final JwtUtil jwtUtil;

    // 작품 목록 조회 (인증 불필요 / 페이지네이션 기본값: 20개, 최신순)
    @GetMapping
    public ResponseEntity<Page<ArtworkResponse>> getArtworks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(artworkService.getArtworks(pageable));
    }

    // 작품 단건 조회 (인증 불필요)
    @GetMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> getArtwork(@PathVariable Long artworkId) {
        return ResponseEntity.ok(artworkService.getArtwork(artworkId));
    }

    // 작품 등록 (인증 필요 - 학생)
    @PostMapping
    public ResponseEntity<ArtworkCreateResponse> create(
            @RequestBody ArtworkCreateRequest request,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artworkService.create(request, principal.userId()));
    }

    // 작품 수정 (인증 필요 - 본인)
    @PatchMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> update(
            @PathVariable Long artworkId,
            @RequestBody ArtworkUpdateRequest request,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.ok(artworkService.update(artworkId, request, principal.userId()));
    }

    // 작품 삭제 (인증 필요 - 본인)
    @DeleteMapping("/{artworkId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long artworkId,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);
        artworkService.delete(artworkId, principal.userId());

        return ResponseEntity.noContent().build();
    }

    // Authorization 헤더에서 JWT 토큰을 추출하고 principal 반환
    private JwtPrincipal extractPrincipal(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        String token = authHeader.substring(7);

        return jwtUtil.getPrincipalFromToken(token);
    }
}
