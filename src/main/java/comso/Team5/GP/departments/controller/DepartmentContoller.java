package comso.Team5.GP.departments.controller;

import comso.Team5.GP.departments.dto.reponse.DepartmentCreateResponse;
import comso.Team5.GP.departments.dto.request.DepartmentCreateRequest;
import comso.Team5.GP.departments.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/departments")
@RequiredArgsConstructor
@RestController
public class DepartmentContoller {

    private final DepartmentService departmentsService;

    @PostMapping("/create")
    public ResponseEntity<DepartmentCreateResponse> create(@RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(departmentsService.create(request));
    }
}
