package comso.Team5.GP.exhibitions.service;

import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.exhibitions.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExhibitionService {

    private final ExhibitionRepository exhibitionsRepository;
    private final DepartmentRepository departmentsRepository;

    @Transactional
    public ExhibitionCreateResponse create(ExhibitionCreateRequest request) {
        Departments department = departmentsRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 학과를 찾을 수 없습니다. id=" + request.getDepartmentId()));

        Exhibitions exhibition = Exhibitions.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .departments(department)
                .build();

        Exhibitions savedExhibition = exhibitionsRepository.save(exhibition);

        ExhibitionCreateResponse response = new ExhibitionCreateResponse(savedExhibition.getExhiId(),
                savedExhibition.getName(), savedExhibition.getDescription(), savedExhibition.getStartDate(),
                savedExhibition.getEndDate(), savedExhibition.getDepartments().getDeptId());

        return response;
    }
}
