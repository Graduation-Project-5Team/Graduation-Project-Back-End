package comso.Team5.GP.util.redis.service;

import java.time.Duration;

public interface RedisSingleDataService {

    int setSingleData(String key, Object value);

    int setSingleData(String key, Object value, Duration duration);

    boolean setIfAbsent(String key, Object value);

    boolean setIfAbsent(String key, Object value, Duration duration);

    Long increment(String key);

    String getSingleData(String key);

    int deleteSingleData(String key);
}
