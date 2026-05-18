package comso.Team5.GP.util.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
// 레디스 환경 설정
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;


    // Redis 연결하기 위한 Connection 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    // Redis 데이터 처리를 위한 템플릿 생성
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // redis 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // redis key-value 직렬화 수행
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // redis Hash key-value 직렬화 수행
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // redis 기본적인 직렬화 수행
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    // Redis에서 List에 대한 다양한 연산 수행
    public ListOperations<String, Object> getListOperations() {
        return this.redisTemplate().opsForList();
    }

    //Redis에서 단일 데이터에 대한 다양한 연산 수행
    public ValueOperations<String, Object> getValueOperations() {
        return this.redisTemplate().opsForValue();
    }

    // Redis에서 데이터 처리 (등록, 삭제, 수정) 및 예외 처리 수행
    public int executeOperation(Runnable operation) {
        try {
            operation.run();
            return 1;
        } catch (Exception e) {
            log.info("Redis 작업 중 오류 발생" + e.getMessage());
            return 0;
        }
    }
}
