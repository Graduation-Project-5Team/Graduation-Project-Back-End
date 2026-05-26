package comso.Team5.GP.users.controller;

import comso.Team5.GP.artworks.dto.response.ArtworkHidingResponse;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.service.AdminService;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    @PostMapping("/artworks/{artworkId}/hiding")
    public ResponseEntity<ArtworkHidingResponse> artworkHding(
            HttpServletRequest request, @PathVariable Long artworkId) {

        JwtPrincipal principal = extractPrincipal(request);

        ArtworkHidingResponse response = adminService.artworkHiding(principal.role(), artworkId);

        return ResponseEntity.ok(response);
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
