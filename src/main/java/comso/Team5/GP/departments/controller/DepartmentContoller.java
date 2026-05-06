package comso.Team5.GP.departments.controller;

import comso.Team5.GP.departments.dto.reponse.DepartmentCreateResponse;
import comso.Team5.GP.departments.dto.request.DepartmentCreateRequest;
import comso.Team5.GP.departments.dto.request.DepartmentUpdateRequest;
import comso.Team5.GP.departments.service.DepartmentService;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import comso.Team5.GP.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/departments")
@RequiredArgsConstructor
@RestController
public class DepartmentContoller {

    private final DepartmentService departmentsService;
    private final JwtUtil jwtUtil;

    // 전체 학과 목록 조회 (인증 불필요)
    @GetMapping
    public ResponseEntity<List<DepartmentCreateResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentsService.getAllDepartments());
    }

    // 학과 단건 조회 (인증 불필요)
    @GetMapping("/{deptId}")
    public ResponseEntity<DepartmentCreateResponse> getDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(departmentsService.getDepartment(deptId));
    }

    // 학과 등록 (인증 필요)
    @PostMapping
    public ResponseEntity<DepartmentCreateResponse> create(
            @RequestBody DepartmentCreateRequest request,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentsService.create(request, principal.userId()));
    }

    // 학과 수정 (인증 필요 - 관리자)
    @PatchMapping("/{deptId}")
    public ResponseEntity<DepartmentCreateResponse> update(
            @PathVariable Long deptId,
            @RequestBody DepartmentUpdateRequest request,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);

        return ResponseEntity.ok(departmentsService.update(deptId, request, principal.userId()));
    }

    // 학과 삭제 (인증 필요 - 관리자)
    @DeleteMapping("/{deptId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long deptId,
            HttpServletRequest httpRequest) {

        JwtPrincipal principal = extractPrincipal(httpRequest);
        departmentsService.delete(deptId, principal.userId());

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
