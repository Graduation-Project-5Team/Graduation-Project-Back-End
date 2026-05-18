package comso.Team5.GP.util.redis.controller;

import comso.Team5.GP.util.redis.dto.request.RedisDeleteValueDto;
import comso.Team5.GP.util.redis.dto.request.RedisGetValueDto;
import comso.Team5.GP.util.redis.dto.request.RedisSetValueDto;
import comso.Team5.GP.util.redis.dto.response.RedisGetValueResponseDto;
import comso.Team5.GP.util.redis.service.RedisSingleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/redis/singleData")
@RestController
@RequiredArgsConstructor
// Redis 데이터를 실제 api를 통해서 확인하기 위한 컨트롤러 (post맨으로 확인 가능)
public class RedisSingleDataController {

    private final RedisSingleDataService redisSingleDataService;

    @PostMapping("/getValue")
    public ResponseEntity<RedisGetValueResponseDto> getValue(@RequestBody RedisGetValueDto dto) {
        RedisGetValueResponseDto response = new RedisGetValueResponseDto(redisSingleDataService.getSingleData(dto.getKey()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/incrementValue")
    public ResponseEntity<Object> incrementValue(@RequestBody RedisGetValueDto dto) {
        return new ResponseEntity<>(redisSingleDataService.increment(dto.getKey()), HttpStatus.OK);
    }

     @PostMapping("/setValue")
    public ResponseEntity<Object> setValue(@RequestBody RedisSetValueDto dto) {
        if (dto.getDuration() == null) {
            return new ResponseEntity<>(redisSingleDataService.setSingleData(dto.getKey(), dto.getValue()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(redisSingleDataService.setSingleData(dto.getKey(), dto.getValue(), dto.getDuration()), HttpStatus.OK);
        }
     }

     @PostMapping("/deleteValue")
    public ResponseEntity<Object> deleteValue(@RequestBody RedisDeleteValueDto dto) {
        return new ResponseEntity<>(redisSingleDataService.deleteSingleData(dto.getKey()), HttpStatus.OK);
     }
}
