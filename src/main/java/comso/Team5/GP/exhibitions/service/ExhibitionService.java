package comso.Team5.GP.exhibitions.service;

import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.exhibitions.repository.ExhibitionRepository;
import comso.Team5.GP.global.exception.departments.DepartmentException;
import comso.Team5.GP.global.exception.departments.DepartmentExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExhibitionService {

    private final ExhibitionRepository exhibitionsRepository;
    private final DepartmentRepository departmentsRepository;

    // 전시 등록
    @Transactional
    public ExhibitionCreateResponse create(ExhibitionCreateRequest request) {
        Departments department = departmentsRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));

        Exhibitions exhibition = Exhibitions.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .departments(department)
                .build();

        Exhibitions savedExhibition = exhibitionsRepository.save(exhibition);

        return toResponse(savedExhibition);
    }

    // 전시 단건 조회
    @Transactional(readOnly = true)
    public ExhibitionCreateResponse getExhibition(Long exhiId) {

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 전시가 존재하지 않습니다. 전시 ID : " + exhiId));

        return toResponse(exhibition);
    }

    // 전체 전시 목록 조회
    @Transactional(readOnly = true)
    public List<ExhibitionCreateResponse> getAllExhibitions() {

        List<Exhibitions> exhibitions = exhibitionsRepository.findAll();

        return exhibitions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Exhibitions 엔티티를 ExhibitionCreateResponse DTO로 변환
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
