package comso.Team5.GP.departments.service;

import comso.Team5.GP.departments.dto.reponse.DepartmentCreateResponse;
import comso.Team5.GP.departments.dto.request.DepartmentCreateRequest;
import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentRepository departmentsRepository;

    @Transactional(readOnly = true)
    public DepartmentCreateResponse create (DepartmentCreateRequest request) {

        Departments dept = new Departments(request.getName());

        departmentsRepository.save(dept);

        return new DepartmentCreateResponse(dept.getDeptId(), dept.getName());
    }
}
