package comso.Team5.GP.artworks.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ArtworkUpdateRequest {
    private String title;
    private String description;
    private int likeCount;
}
