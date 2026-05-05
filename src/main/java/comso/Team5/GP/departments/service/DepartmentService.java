package comso.Team5.GP.departments.service;

import comso.Team5.GP.departments.dto.reponse.DepartmentCreateResponse;
import comso.Team5.GP.departments.dto.request.DepartmentCreateRequest;
import comso.Team5.GP.departments.dto.request.DepartmentUpdateRequest;
import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import comso.Team5.GP.global.exception.departments.DepartmentException;
import comso.Team5.GP.global.exception.departments.DepartmentExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentRepository departmentsRepository;
    private final UserRepository userRepository;

    // 학과 등록 (관리자만 가능)
    @Transactional
    public DepartmentCreateResponse create(DepartmentCreateRequest request, Long userId) {

        checkAdmin(userId);

        Departments dept = new Departments(request.getName());

        departmentsRepository.save(dept);

        return new DepartmentCreateResponse(dept.getDeptId(), dept.getName());
    }

    // 학과 단건 조회
    @Transactional(readOnly = true)
    public DepartmentCreateResponse getDepartment(Long deptId) {

        Departments dept = departmentsRepository.findById(deptId)
                .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));

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

    // 학과 수정 (관리자만 가능)
    @Transactional
    public DepartmentCreateResponse update(Long deptId, DepartmentUpdateRequest request, Long userId) {

        checkAdmin(userId);

        Departments dept = departmentsRepository.findById(deptId)
                .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));

        if (request.getName() != null) dept.setName(request.getName());

        return new DepartmentCreateResponse(dept.getDeptId(), dept.getName());
    }

    // 학과 삭제 (관리자만 가능)
    @Transactional
    public void delete(Long deptId, Long userId) {

        checkAdmin(userId);

        Departments dept = departmentsRepository.findById(deptId)
                .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));

        departmentsRepository.delete(dept);
    }

    private void checkAdmin(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new DepartmentException(DepartmentExceptionCode.NOT_ADMIN);
        }
    }
}
