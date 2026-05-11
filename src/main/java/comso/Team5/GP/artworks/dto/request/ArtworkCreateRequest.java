package comso.Team5.GP.artworks.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtworkCreateRequest {
    private Long exhiId;

    private String title;

    private String description;
}
