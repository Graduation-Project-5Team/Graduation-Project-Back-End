package comso.Team5.GP.artworks.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ArtworkCreateRequest {

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private String content;

    // TODO images[] : 이미지 테이블 구현 완료 후 추가 예정
}
