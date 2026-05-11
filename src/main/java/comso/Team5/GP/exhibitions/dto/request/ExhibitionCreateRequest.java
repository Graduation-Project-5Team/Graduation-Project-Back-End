package comso.Team5.GP.exhibitions.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionCreateRequest {
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long departmentId;
}
