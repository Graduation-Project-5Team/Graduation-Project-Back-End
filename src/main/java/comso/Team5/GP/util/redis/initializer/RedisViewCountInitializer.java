package comso.Team5.GP.util.redis.initializer;

import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.util.redis.service.RedisSingleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisViewCountInitializer {

    private final ArtworkRepository artworkRepository;
    private final RedisSingleDataService redisSingleDataService;

    // 프로젝트를 재실행 / 실행하게 될 경우 해당 이벤트 메서드가 실행
    @EventListener(ApplicationReadyEvent.class)
    // mysql 사용 시 조회수의 값을 유지하기 위한 메서드
    public void initViewCountFromDatabase() {
        List<Artworks> artworks = artworkRepository.findAll();

        for (Artworks artwork : artworks) {
            String key = "artwork:views:" + artwork.getArtworkId();
            redisSingleDataService.setSingleData(key, artwork.getViews());
        }
    }
}
