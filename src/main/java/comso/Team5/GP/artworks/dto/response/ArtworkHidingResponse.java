package comso.Team5.GP.artworks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtworkHidingResponse {
    private String message;

    private boolean isHidden;
}
