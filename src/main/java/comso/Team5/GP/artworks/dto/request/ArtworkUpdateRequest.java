package comso.Team5.GP.artworks.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtworkUpdateRequest {

    private String title;

    private String description;

    // TODO images[] : 이미지 테이블 구현 완료 후 추가 예정
}
