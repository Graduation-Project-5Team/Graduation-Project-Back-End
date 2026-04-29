package comso.Team5.GP.artworks.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtworkCreateRequest {
    private Long userId;

    private Long exhiId;

    private String title;

    private String description;
}
