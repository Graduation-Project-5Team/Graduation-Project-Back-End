package comso.Team5.GP.departments.service;

import comso.Team5.GP.departments.dto.reponse.DepartmentCreateResponse;
import comso.Team5.GP.departments.dto.request.DepartmentCreateRequest;
import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentRepository departmentsRepository;

    // 학과 등록
    @Transactional
    public DepartmentCreateResponse create(DepartmentCreateRequest request) {

        Departments dept = new Departments(request.getName());

        departmentsRepository.save(dept);

        return new DepartmentCreateResponse(dept.getDeptId(), dept.getName());
    }

    // 학과 단건 조회
    @Transactional(readOnly = true)
    public DepartmentCreateResponse getDepartment(Long deptId) {

        Departments dept = departmentsRepository.findById(deptId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학과가 존재하지 않습니다. 학과 ID : " + deptId));

        return new DepartmentCreateResponse(dept.getDeptId(), dept.getName());
    }

    // 전체 학과 목록 조회
    @Transactional(readOnly = true)
    public List<DepartmentCreateResponse> getAllDepartments() {

        List<Departments> departments = departmentsRepository.findAll();

        return departments.stream()
                .map(dept -> new DepartmentCreateResponse(dept.getDeptId(), dept.getName()))
                .collect(Collectors.toList());
    }
}
