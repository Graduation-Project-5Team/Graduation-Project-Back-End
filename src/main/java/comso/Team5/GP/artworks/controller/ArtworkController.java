package comso.Team5.GP.artworks.controller;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.request.ArtworkUpdateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkDetailResponse;
import comso.Team5.GP.artworks.dto.response.ArtworkResponse;
import comso.Team5.GP.artworks.service.ArtworkService;
import comso.Team5.GP.comments.dto.response.CommentResponse;
import comso.Team5.GP.comments.service.CommentService;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/artworks")
@RestController
public class ArtworkController {

    private final ArtworkService artworkService;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    // 작품 목록 조회 (인증 불필요 / 페이지네이션 기본값: 20개, 최신순)
    @GetMapping
    public ResponseEntity<Page<ArtworkResponse>> getArtworks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(artworkService.getArtworks(pageable));
    }

    // 작품 세부 정보 조회 (인증 - 비로그인/로그인 분류)
    @GetMapping("/{artworkId}/detail")
    public ResponseEntity<ArtworkDetailResponse> getArtworkDetail(@PathVariable Long artworkId,
                                                                           HttpServletRequest request) {

        String viewerKey = createViewerKey(request);

        return ResponseEntity.ok(artworkService.getArtworkDetail(artworkId, viewerKey));
    }

    // 작품 단건 조회
    @GetMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> getArtwork(@PathVariable Long artworkId) {
        return ResponseEntity.ok(artworkService.getArtwork(artworkId));
    }

    // 작품 댓글 목록 조회 (인증 불필요)
    @GetMapping("/{artworkId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByArtwork(@PathVariable Long artworkId) {
        return ResponseEntity.ok(commentService.getCommentsByArtwork(artworkId));
    }

    // 작품 등록 (인증 필요 - 학생)
    @PostMapping("/create")
    public ResponseEntity<ArtworkCreateResponse> create(
            @RequestPart("request") ArtworkCreateRequest request,
            HttpServletRequest httpRequest,
            @RequestPart("images") List<MultipartFile> multipartFile) {

        List<MultipartFile> files = multipartFile;

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artworkService.create(request, principal.userId(), files));
    }

    // 작품 수정 (인증 필요 - 본인)
    @PatchMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> update(
            @PathVariable Long artworkId,
            @RequestPart(value = "request", required = false) ArtworkUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        List<MultipartFile> files = images;

        return ResponseEntity.ok(artworkService.update(artworkId, request, principal.userId(), files));
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

    // 좋아요 상태 조회 (인증 필요)
    @GetMapping("/{artworkId}/like/status")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable Long artworkId,
                                                  HttpServletRequest request) {
        JwtPrincipal principal = extractPrincipal(request);
        return ResponseEntity.ok(artworkService.getLikeStatus(artworkId, principal.userId()));
    }

    // 좋아요 추가
    @PostMapping("/{artworkId}/like")
    public ResponseEntity<Void> addLike(@PathVariable Long artworkId, HttpServletRequest request) {

        JwtPrincipal principal = extractPrincipal(request);

        artworkService.addLike(artworkId, principal.userId());

        return ResponseEntity.noContent().build();
    }

    // 좋아요 삭제
    @DeleteMapping("/{artworkId}/like")
    public ResponseEntity<Void> removeLike(@PathVariable Long artworkId, HttpServletRequest request) {
        JwtPrincipal principal = extractPrincipal(request);

        artworkService.removeLike(artworkId, principal.userId());

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

    // 비로그인 사용자의 Ip를 확인하기 위한 메서드
    private String getClienIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // redis에 저장할 viewer키 생성하는 메서드
    private String createViewerKey(HttpServletRequest request) {
        String viewerKey;

        String authHeader = request.getHeader("Authorization");

        // 사용자가 로그인한 사용자이면 로그인
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            Long userId = extractPrincipal(request).userId();
            viewerKey = "user:" + userId;
        } else {
            String ip = getClienIp(request);
            String userAgent = request.getHeader("User-Agent");

            viewerKey = "user" + ip + ":" + userAgent;
        }
        return viewerKey;
    }
}