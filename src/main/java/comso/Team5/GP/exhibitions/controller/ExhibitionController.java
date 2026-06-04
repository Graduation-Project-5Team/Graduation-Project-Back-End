package comso.Team5.GP.exhibitions.controller;

import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionUpdateRequest;
import comso.Team5.GP.exhibitions.service.ExhibitionService;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/exhibitions")
@RestController
public class ExhibitionController {

    private final ExhibitionService exhibitionsService;
    private final JwtUtil jwtUtil;

    // 전체 전시 목록 조회 (인증 불필요)
    @GetMapping
    public ResponseEntity<List<ExhibitionCreateResponse>> getAllExhibitions() {
        return ResponseEntity.ok(exhibitionsService.getAllExhibitions());
    }

    // 전시 단건 조회 (인증 불필요)
    @GetMapping("/{exhiId}")
    public ResponseEntity<ExhibitionCreateResponse> getExhibition(@PathVariable Long exhiId) {
        return ResponseEntity.ok(exhibitionsService.getExhibition(exhiId));
    }

    // 전시 등록 (인증 필요 - 관리자)
    @PostMapping
    public ResponseEntity<ExhibitionCreateResponse> create(
            @RequestPart("image") MultipartFile image,
            @RequestPart("request") ExhibitionCreateRequest request,
            HttpServletRequest httpRequest) {

        MultipartFile file = image;

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exhibitionsService.create(request, principal.userId(), file));
    }

    // 전시 수정 (인증 필요 - 관리자)
    @PatchMapping("/{exhiId}")
    public ResponseEntity<ExhibitionCreateResponse> update(
            @PathVariable Long exhiId,
            @RequestPart(value = "request", required = false) ExhibitionUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.ok(exhibitionsService.update(exhiId, request, principal.userId(), image));
    }

    // 전시 삭제 (인증 필요 - 관리자)
    @DeleteMapping("/{exhiId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long exhiId,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);
        exhibitionsService.delete(exhiId, principal.userId());

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
