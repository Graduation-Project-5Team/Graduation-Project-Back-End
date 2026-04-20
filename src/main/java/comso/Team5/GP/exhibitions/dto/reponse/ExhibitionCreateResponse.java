package comso.Team5.GP.exhibitions.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionCreateResponse {
    private Long exhiId;

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long departmentId;


}
