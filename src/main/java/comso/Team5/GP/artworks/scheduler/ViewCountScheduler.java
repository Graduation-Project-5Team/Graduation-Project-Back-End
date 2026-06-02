package comso.Team5.GP.artworks.scheduler;

import comso.Team5.GP.artworks.service.ArtworkViewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    // 조회수 서비스명은 Views 표기를 사용한다.
    private final ArtworkViewsService artworkViewsService;

    // 5분 주기로 작품의 조회수를 데이터베이스에 동기화하는 스케줄러
    @Scheduled(fixedRate = 300000)
    public void syncViewCounts() {
        artworkViewsService.syncAllViewCountsToDatabase();
    }
}
