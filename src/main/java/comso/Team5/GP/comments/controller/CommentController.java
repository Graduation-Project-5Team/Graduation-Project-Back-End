package comso.Team5.GP.comments.controller;

import comso.Team5.GP.comments.dto.request.CommentCreateRequest;
import comso.Team5.GP.comments.dto.request.CommentUpdateRequest;
import comso.Team5.GP.comments.dto.response.CommentCreateResponse;
import comso.Team5.GP.comments.dto.response.CommentResponse;
import comso.Team5.GP.comments.service.CommentService;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    // 댓글 등록 (인증 필요)
    @PostMapping
    public ResponseEntity<CommentCreateResponse> create(
            @RequestBody CommentCreateRequest request,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(request, principal.userId()));
    }

    // 댓글 수정 (인증 필요 - 본인)
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.ok(commentService.update(commentId, request, principal.userId()));
    }

    // 댓글 삭제 (인증 필요 - 본인)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);
        commentService.delete(commentId, principal.userId());

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
