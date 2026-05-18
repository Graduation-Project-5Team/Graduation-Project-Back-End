package comso.Team5.GP.util.redis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedisDeleteValueDto {
    private String key;
}
