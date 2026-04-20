package comso.Team5.GP.departments.controller;

import comso.Team5.GP.departments.dto.reponse.DepartmentCreateResponse;
import comso.Team5.GP.departments.dto.request.DepartmentCreateRequest;
import comso.Team5.GP.departments.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/departments")
@RequiredArgsConstructor
@RestController
public class DepartmentContoller {

    private final DepartmentService departmentsService;

    // 학과 등록
    @PostMapping("/create")
    public ResponseEntity<DepartmentCreateResponse> create(@RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(departmentsService.create(request));
    }

    // 학과 단건 조회
    @GetMapping("/{deptId}")
    public ResponseEntity<DepartmentCreateResponse> getDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(departmentsService.getDepartment(deptId));
    }

    // 전체 학과 목록 조회
    @GetMapping
    public ResponseEntity<List<DepartmentCreateResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentsService.getAllDepartments());
    }
}
