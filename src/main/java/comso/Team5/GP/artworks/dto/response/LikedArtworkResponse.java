package comso.Team5.GP.artworks.dto.response;

import comso.Team5.GP.artworks.entity.ArtworkLike;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LikedArtworkResponse {

    private Long artworkId;
    private String title;
    private String artistNickname;
    private String departmentName;
    private int likeCount;
    private List<String> imageUrls; // 이미지 파일명 목록

    public static LikedArtworkResponse from(ArtworkLike like) {
        List<String> images = like.getArtwork().getImageUrl().stream()
                .map(img -> img.getImageUrl())
                .toList();

        return new LikedArtworkResponse(
                like.getArtwork().getArtworkId(),
                like.getArtwork().getTitle(),
                like.getArtwork().getUsers().getNickname(),
                like.getArtwork().getUsers().getDepartments().getName(),
                like.getArtwork().getLikeCount(),
                images
        );
    }
}
