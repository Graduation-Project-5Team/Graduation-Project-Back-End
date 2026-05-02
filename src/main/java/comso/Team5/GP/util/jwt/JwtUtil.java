package comso.Team5.GP.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Component
public class JwtUtil {


    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${jwt.access.expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.refresh.expiration}")
    private long REFRESH_EXPIRATION;

    @Value("${jwt.issuer:GP}")
    private String ISSUER;

    public String generateToken(String id) {
        Instant now = Instant.now();
        long issuedAt = now.getEpochSecond();
        long expiresAt = now.plusMillis(ACCESS_EXPIRATION).getEpochSecond();

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        String escapedUserId = escapeJson(id);
        String payloadJson = "{\"sub\":\""+ escapedUserId + "\""
                + ",\"id\":\"" + id + "\""
                + ",\"iss\":\"" + ISSUER + "\""
                + ",\"iat\":" + issuedAt
                + ",\"exp\":" + expiresAt
                +"}";

        String encodeHeader = encodeBase64Url(headerJson);
        String encodePayload = encodeBase64Url(payloadJson);
        String content = encodeHeader + "." + encodePayload;
        String signature = sign(content);

        return content + "." + signature;
    }

    public String generateRefresh(Long userId) {
        Instant now = Instant.now();
        long issuedAt = now.getEpochSecond();
        long expiresAt = now.plusMillis(REFRESH_EXPIRATION).getEpochSecond();

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
;
        String payloadJson = "{\"id\":\"" + userId + "\""
                + ",\"iss\":\"" + ISSUER + "\""
                + ",\"iat\":" + issuedAt
                + ",\"exp\":" + expiresAt
                +"}";

        String encodeHeader = encodeBase64Url(headerJson);
        String encodePayload = encodeBase64Url(payloadJson);
        String content = encodeHeader + "." + encodePayload;
        String signature = sign(content);

        return content + "." + signature;
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

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    public String getUserIdFromToken(String token) {
        return verifyToken(token).getSubject();
    }

    /**
     * 페이로드의 숫자 id(PK, user_id)와 sub(로그인 id)를 한 번의 검증으로 반환.
     */
    public JwtPrincipal getPrincipalFromToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        var idClaim = decodedJWT.getClaim("id");
        String subject = decodedJWT.getSubject();
        if (idClaim.isNull() || subject == null || subject.isBlank()) {
            throw new UserException(UserExceptionCode.INVALID_TOKEN);
        }
        return new JwtPrincipal(idClaim.asLong(), subject);
    }

    /**
     * 리프레시 토큰에서 userId(PK)를 추출합니다.
     */
    public Long getUserIdFromRefreshToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        var idClaim = decodedJWT.getClaim("id");
        if (idClaim.isNull()) {
            throw new UserException(UserExceptionCode.INVALID_TOKEN);
        }
        return idClaim.asLong();
    }

    private DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return jwtVerifier.verify(token);
        } catch (TokenExpiredException e) {
            log.error("토큰이 만료되었습니다.");
            throw new UserException(UserExceptionCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.error("유효하지 않는 토큰입니다.");
            throw new UserException(UserExceptionCode.INVALID_TOKEN);
        }
    }

    // 토큰 검증 후 UserId와 id를 반환하는 객체
    public record JwtPrincipal(Long userId, String id) {
    }

}
