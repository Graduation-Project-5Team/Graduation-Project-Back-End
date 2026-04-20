package comso.Team5.GP.exhibitions.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionCreateRequest {
    private String name;

    private String description;

    // TODO private String thumbnailImage;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long departmentId;
}
