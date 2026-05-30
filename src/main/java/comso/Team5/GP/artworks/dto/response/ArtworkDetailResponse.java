package comso.Team5.GP.artworks.dto.response;

import comso.Team5.GP.users.dto.response.UserArtworkDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ArtworkDetailResponse {
    private Long artworkId;

    private UserArtworkDetailResponseDto user;

    private Long exhiId;

    private String title;

    private String description;

    private List<ArtworkImagesResponse> images;

    private int likeCount;

    private Long views;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
