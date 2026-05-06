package comso.Team5.GP.exhibitions.service;

import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionUpdateRequest;
import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.exhibitions.repository.ExhibitionRepository;
import comso.Team5.GP.global.exception.departments.DepartmentException;
import comso.Team5.GP.global.exception.departments.DepartmentExceptionCode;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionException;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionExceptionCode;
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
@Transactional
public class ExhibitionService {

    private final ExhibitionRepository exhibitionsRepository;
    private final DepartmentRepository departmentsRepository;
    private final UserRepository userRepository;

    // 전시 등록 (관리자만 가능)
    public ExhibitionCreateResponse create(ExhibitionCreateRequest request, Long userId) {

        checkAdmin(userId);

        Departments department = departmentsRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));

        Exhibitions exhibition = Exhibitions.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .departments(department)
                .build();

        return toResponse(exhibitionsRepository.save(exhibition));
    }

    // 전시 단건 조회
    @Transactional(readOnly = true)
    public ExhibitionCreateResponse getExhibition(Long exhiId) {

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        return toResponse(exhibition);
    }

    // 전체 전시 목록 조회
    @Transactional(readOnly = true)
    public List<ExhibitionCreateResponse> getAllExhibitions() {

        return exhibitionsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 전시 수정 (관리자만 가능)
    public ExhibitionCreateResponse update(Long exhiId, ExhibitionUpdateRequest request, Long userId) {

        checkAdmin(userId);

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        // 요청에 포함된 필드만 수정 (null인 필드는 기존 값 유지)
        if (request.getName() != null) exhibition.setName(request.getName());
        if (request.getDescription() != null) exhibition.setDescription(request.getDescription());
        if (request.getStartDate() != null) exhibition.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) exhibition.setEndDate(request.getEndDate());
        if (request.getDepartmentId() != null) {
            // 학과 ID 변경 시 존재 여부 검증 후 교체
            Departments department = departmentsRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));
            exhibition.setDepartments(department);
        }

        return toResponse(exhibition);
    }

    // 전시 삭제 (관리자만 가능)
    public void delete(Long exhiId, Long userId) {

        checkAdmin(userId);

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        exhibitionsRepository.delete(exhibition);
    }

    private void checkAdmin(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new ExhibitionException(ExhibitionExceptionCode.NOT_ADMIN);
        }
    }

    private ExhibitionCreateResponse toResponse(Exhibitions exhibition) {
        return new ExhibitionCreateResponse(
                exhibition.getExhiId(),
                exhibition.getName(),
                exhibition.getDescription(),
                exhibition.getStartDate(),
                exhibition.getEndDate(),
                exhibition.getDepartments().getDeptId()
        );
    }
}
