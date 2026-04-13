package comso.Team5.GP.util.jwt;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtUtil {


    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${jwt.access.expiration}")
    private long ACCESS_EXPIRATION;

    public String generateToken(Long id, String userId) {
        Instant now = Instant.now();
        long issuedAt = now.getEpochSecond();
        long expiresAt = now.plusMillis(ACCESS_EXPIRATION).getEpochSecond();

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        String escapeUserId = esacpeJson(userId);
        String payloadJson = "{\"sub:\":\""+ escapeUserId + "\""
                + ",\"id\":" + id
                + ",\"user_id\":\"" + escapeUserId + "\""
                + ",\"jat\":" + issuedAt
                + ",\"exp\":" + expiresAt
                +"}";

        String encodeHeader = encodeBase64Url(headerJson);
        String encodePayload = encodeBase64Url(payloadJson);
        String content = encodeHeader + encodePayload;
        String signature = sign(content);

        return content + signature;

    }

    // 토큰 만료 시간이 얼마나 오래 유효한지 표시할 때 사용하는 값에 대한 메서드
    public long getExpirationSeconds() {
        // 토큰 만료시간이 얼마나 오래 유효한지 표시할 때 사용하는 값
        return ACCESS_EXPIRATION / 1000;
    }

    // base64Url 인코딩 메서드
    public String encodeBase64Url(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    //서명 생성 메서드
    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            // SHA256 알고리즘을 사용해서 Secret 토큰 서명 생성에 사용
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            byte[] signtureBytes = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signtureBytes);
        } catch (Exception e) {
            throw new IllegalStateException("JWT 서명 생성에 실패 했습니다.");
        }
    }

    private String esacpeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }


}
