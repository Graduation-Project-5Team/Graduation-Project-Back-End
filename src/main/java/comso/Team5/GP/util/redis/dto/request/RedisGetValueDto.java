package comso.Team5.GP.util.redis.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RedisGetValueDto {

    private String key;
}
