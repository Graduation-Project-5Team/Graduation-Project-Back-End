package comso.Team5.GP.util.redis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public class RedisSetValueDto {

    private String key;

    private Object value;

    private Duration duration;
}
