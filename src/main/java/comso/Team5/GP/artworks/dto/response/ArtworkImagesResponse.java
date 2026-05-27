package comso.Team5.GP.artworks.dto.response;

import comso.Team5.GP.artworks.entity.ArtworkImages;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ArtworkImagesResponse {

    private Long artworkImageId;

    private String imageUrl;

    private int sortOrder;

    public static ArtworkImagesResponse from(ArtworkImages image) {
        return new ArtworkImagesResponse(
                image.getArtworkImageId(),
                image.getImageUrl(),
                image.getSortOrder());
    }
}
