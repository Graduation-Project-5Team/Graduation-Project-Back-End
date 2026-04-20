package comso.Team5.GP.artworks.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArtworkCreateRequest {
    private Long userId;

    private Long exhiId;

    private String title;

    private String description;

    private int likeCount;
}
