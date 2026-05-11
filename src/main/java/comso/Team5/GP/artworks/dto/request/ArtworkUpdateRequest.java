package comso.Team5.GP.artworks.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtworkUpdateRequest {

    private String title;

    private String description;
}
