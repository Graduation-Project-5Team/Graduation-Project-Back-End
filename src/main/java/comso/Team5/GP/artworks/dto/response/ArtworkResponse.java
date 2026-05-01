package comso.Team5.GP.artworks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArtworkResponse {

    private Long artworkId;

    private Long userId;

    private Long exhiId;

    private String title;

    private String description;

    private int likeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
