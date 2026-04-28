package comso.Team5.GP.artworks.dto.response;

import comso.Team5.GP.artworks.entity.Artworks;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ArtworkResponse {
    private final Long artworkId;
    private final Long userId;
    private final Long exhiId;
    private final String title;
    private final String description;
    private final int likeCount;
    private final LocalDate createdAt;
    private final LocalDate updatedAt;

    public ArtworkResponse(Artworks artwork) {
        this.artworkId = artwork.getArtworkId();
        this.userId = artwork.getUsers().getUserId();
        this.exhiId = artwork.getExhibitions().getExhiId();
        this.title = artwork.getTitle();
        this.description = artwork.getDescription();
        this.likeCount = artwork.getLikeCount();
        this.createdAt = artwork.getCreatedAt();
        this.updatedAt = artwork.getUpdatedAt();
    }
}
