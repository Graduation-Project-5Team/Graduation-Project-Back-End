package comso.Team5.GP.util.redis;

import comso.Team5.GP.util.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class Redishandller {

    private final RedisConfig redisConfig;

    // 아직 사용 안 함
    public ListOperations<String, Object> getListOperations() {
        return redisConfig.getListOperations();
    }

    public ValueOperations<String, Object> getValueOperations() {
        return redisConfig.getValueOperations();
    }

    public int executeOperations(Runnable operation) {
        try{
            operation.run();
            return 1;
        } catch(Exception e) {
            log.error("Redis 처리 중 오류 발생 :" + e.getMessage());
            return 0;
        }
    }
}
