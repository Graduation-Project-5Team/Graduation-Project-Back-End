package comso.Team5.GP.users.controller;

import comso.Team5.GP.artworks.dto.response.ArtworkHidingResponse;
import comso.Team5.GP.comments.dto.response.CommentListResponse;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.dto.request.AdminUserDepartmentsChangeRequest;
import comso.Team5.GP.users.dto.request.UserRoleChangeRequest;
import comso.Team5.GP.users.dto.response.UserListResponse;
import comso.Team5.GP.users.dto.response.UserRoleChangeResponse;
import comso.Team5.GP.users.service.AdminService;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    // 관리자 페이지 사용자 전체 조회
    @GetMapping("/user-list")
    public ResponseEntity<Page<UserListResponse>> userList(HttpServletRequest request,
                                                           @RequestParam(required = false) String keyword,
                                                           @PageableDefault(size = 20, sort = "nickname", direction = Sort.Direction.DESC) Pageable pageable) {
        JwtPrincipal principal = extractPrincipal(request);

        Page<UserListResponse> userList = adminService.getUserList(principal.role(), keyword, pageable);

        return ResponseEntity.ok(userList);
    }

    // 관리자 페이지 댓글 전체 조회
    @GetMapping("/comment-list")
    public ResponseEntity<Page<CommentListResponse>> commentList(HttpServletRequest request,
                                                                 @RequestParam(required = false) String keyword,
                                                                 @PageableDefault(size = 20, sort = "commentId", direction = Sort.Direction.DESC) Pageable pageable) {

        JwtPrincipal principal = extractPrincipal(request);

        Page<CommentListResponse> commentList = adminService.getCommentList(principal.role(), keyword, pageable);

        return ResponseEntity.ok(commentList);
    }

    // 권한 변경
    @PostMapping("/role-change")
    public ResponseEntity<UserRoleChangeResponse> changeRole(
            HttpServletRequest request,
            @Valid @RequestBody UserRoleChangeRequest requestDto) {
        JwtPrincipal jwtPrincipal = extractPrincipal(request);

        UserRoleChangeResponse response = adminService.changeRole(jwtPrincipal, requestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/artworks/{artworkId}/hiding")
    public ResponseEntity<ArtworkHidingResponse> artworkHding(
            HttpServletRequest request, @PathVariable Long artworkId) {

        JwtPrincipal principal = extractPrincipal(request);

        ArtworkHidingResponse response = adminService.artworkHiding(principal.role(), artworkId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/departments-change")
    public ResponseEntity<Map<String, String>> userDepartmentsChange(
            HttpServletRequest request,@Valid @RequestBody AdminUserDepartmentsChangeRequest dto) {
        JwtPrincipal principal = extractPrincipal(request);

        adminService.userDepartmentsChange(principal, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "학과 변경이 완료되었습니다."));
    }

    private JwtPrincipal extractPrincipal(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null && !authHeader.startsWith("Bearer ")) {
            throw new UserException(UserExceptionCode.AUTH_HEADER_MISSING);
        }

        String token = authHeader.substring(7);

        return jwtUtil.getPrincipalFromToken(token);
    }
}
