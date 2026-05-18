package comso.Team5.GP.util.redis.service;

import comso.Team5.GP.util.config.RedisConfig;
import comso.Team5.GP.util.redis.Redishandller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
// RedisSingleService로 만든 인터페이스(추상화)를 통해 로직 구현
@RequiredArgsConstructor
@Service
public class RedisSingleDataServiceImplements implements RedisSingleDataService{

    private final Redishandller redishandller;
    private final RedisConfig redisConfig;

    // redis 값 변경
    @Override
    public int setSingleData(String key, Object value) {
        return redishandller.executeOperations(() -> redishandller.getValueOperations().set(key, String.valueOf(value)));
    }

    // redis 값 변경(만료시간 포함)
    @Override
    public int setSingleData(String key, Object value, Duration duration) {
        return redishandller.executeOperations(() -> redishandller.getValueOperations().set(key, String.valueOf(value), duration));
    }

    // 해당 key가 존재하는지 확인
    @Override
    public boolean setIfAbsent(String key, Object value) {
        Boolean result = redishandller.getValueOperations().setIfAbsent(key, String.valueOf(value));
        return Boolean.TRUE.equals(result);
    }

    // 해당 key가 존재하는지 확인 (만료시간 포함)
    @Override
    public boolean setIfAbsent(String key, Object value, Duration duration) {
        Boolean result = redishandller.getValueOperations().setIfAbsent(key, String.valueOf(value), duration);
        return Boolean.TRUE.equals(result);
    }

    // key의 값을 기준으로 해당 밸류에 값을 증감
    @Override
    public Long increment(String key) {
        return redishandller.getValueOperations().increment(key);
    }

    // key의 값을 기준으로 해당 밸류를 가져오는 메서드
    @Override
    public String getSingleData(String key) {
        log.info("key : " + key);
        Object value = redishandller.getValueOperations().get(key);
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    // key를 이용해 value와 key를 삭제하는 메서드
    @Override
    public int deleteSingleData(String key) {
        return redishandller.executeOperations(() -> redisConfig.redisTemplate().delete(key
        ));
    }
}
