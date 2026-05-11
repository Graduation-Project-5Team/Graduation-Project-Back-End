package comso.Team5.GP.artworks.dto.response;

import comso.Team5.GP.artworks.entity.ArtworkImages;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ArtworkResponse {

    private Long artworkId;

    private Long userId;

    private Long exhiId;

    private String title;

    private String description;

    private List<String> imageUrl;

    private int likeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
