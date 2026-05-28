package comso.Team5.GP.artworks.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ArtworkUpdateRequest {

    private String title;

    private String description;

    // 수정 후에 남겨두도록 기존 이미지 id 목록 리퀘스트로 추가
    private List<Long> keepImageIds;
}
