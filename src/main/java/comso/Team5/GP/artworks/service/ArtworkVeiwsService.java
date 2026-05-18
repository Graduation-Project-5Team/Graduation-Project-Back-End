package comso.Team5.GP.artworks.service;

import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import comso.Team5.GP.util.redis.service.RedisSingleDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ArtworkVeiwsService {

    // 비로그인(ip) / 로그인 사용자가 해당 작품의 게시글을 조회 시 만료 시간 24시간 등록
    private static final Duration VIEW_DUPLICATE_TTL = Duration.ofHours(24);
    // 해당 작품에 대한 조회수 값에 대한 redis key
    private static final String VIEW_COUNT_KEY_PREFIX = "artwork:views:";
    // 해당 사용자(ip / 로그인)가 해당 작품에 조회한 기록이 있는지 redis에 저장하는 redis key
    private static final String VIEWER_KEY_PREFIX = "artwork:viewed:";

    private final ArtworkRepository artworkRepository;
    private final RedisSingleDataService redisSingleDataService;

    // 조회수 증감 시키는 메서드
    public Long increaseViewCount(Long artworkId, String viewerKey) {
        initViewCountIfAbsent(artworkId);

        String viewedKey = createViewedKey(artworkId, viewerKey);

        // 해당 작품의 id와 viewerkey를 통해 조회를 한 기록이 있는지 확인하는 메서드
        boolean firstView = redisSingleDataService.setIfAbsent(viewedKey, "1", VIEW_DUPLICATE_TTL);

        // 조회한 기록이 존재할 시 조회 기록 가져옴
        if (!firstView) {
            return getViewCount(artworkId);
        }

        // 조회수 증감
        return redisSingleDataService.increment(createViewCountKey(artworkId));
    }

    public Long getViewCount(Long artworkId) {
        // 해당 작품에 대한 조회 변수 추가
        String viewCount = redisSingleDataService.getSingleData(createViewCountKey(artworkId));

        // 조회가 존재하면 조회값 리턴
        if (!viewCount.isBlank()) {
            return Long.parseLong(viewCount);
        }

        // 조회 존재하지 않으면 db에서 값을 가져와 리턴
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));
        return (long) artwork.getViews();
    }

    // 조회수를 저장하고 있는 redis를 데이터베이스에 동기화
    @Transactional
    public void syncAllViewCountsToDatabase() {
        List<Artworks> artworks = artworkRepository.findAll();

        for (Artworks artwork : artworks) {
            Long redisViewCount = getViewCount(artwork.getArtworkId());
            artwork.setViews(redisViewCount.intValue());
        }
    }

    private void initViewCountIfAbsent(Long artworkId) {
        String viewCountKey = createViewCountKey(artworkId);

        // 해당 작품의 대한 조회수 키가 존재할 시 그냥 리턴
        if (!redisSingleDataService.getSingleData(viewCountKey).isBlank()) {
            return;
        }

        //없을 경우 작품을 조회 후 redis에 true로 저장
        Artworks artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        redisSingleDataService.setIfAbsent(viewCountKey, artwork.getViews());
    }

    // artwork:views:1
    private String createViewCountKey(Long artworkId) {
        return VIEW_COUNT_KEY_PREFIX + artworkId;
    }

    // artwork:viewed:1:ip / artwork:viewed:1:user:1
    private String createViewedKey(Long artworkId, String viewerKey) {
        return VIEWER_KEY_PREFIX + artworkId + ":" + viewerKey;
    }
}
