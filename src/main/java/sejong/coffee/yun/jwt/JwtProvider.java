package sejong.coffee.yun.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.util.jwt.JwtUtil;

import java.util.Date;

import static sejong.coffee.yun.util.jwt.JwtUtil.getFormatToken;

@Slf4j
@Component
public class JwtProvider {

    private final Long accessExpireTime;
    private final Long refreshExpireTime;
    private final String key;

    public JwtProvider(@Value("${jwt.key}") String key,
                       @Value("${jwt.expireTime.access}") Long accessExpireTime,
                       @Value("${jwt.expireTime.refresh}") Long refreshExpireTime){

        log.info("[key] = {}, [accessExpireTime] = {}, [refreshExpireTime] = {}",
                key, accessExpireTime, refreshExpireTime);

        this.accessExpireTime = accessExpireTime;
        this.refreshExpireTime = refreshExpireTime;
        this.key = JwtUtil.getSigningKey(key);
    }

    public String createAccessToken(Member member) {
        return createTokenLogic(member, accessExpireTime);
    }
    public String createRefreshToken(Member member) {
        return createTokenLogic(member, refreshExpireTime);
    }

    /**
     * 토큰 생성
     * @param member 유저
     * @param expireTime 토큰 만료기간
     * @return token
     */
    private String createTokenLogic(Member member, Long expireTime) {

        Claims claims = Jwts.claims();
        claims.put("id", member.getId());

        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + expireTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    private String tokenPayloadExtract(String jwt) {
        String token = getFormatToken(jwt);

        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get("id").toString();
    }

    public Long fetchRefreshTokenExpireTime() {
        return this.refreshExpireTime;
    }

    public Long fetchAccessTokenExpireTime() {
        return this.accessExpireTime;
    }

    /**
     * 토큰 유효성, 만료 체크
     * @param jwt 토큰
     * @return 유효성 통과 true, 실패 false
     */
    public boolean tokenExpiredCheck(String jwt) {

        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
            log.info("[expireTime] = {}", claims.getExpiration());
            log.info("[subject] = {}", claims.getSubject());
        } catch (JwtException | NullPointerException e) {
            log.error("token error!! = {} {}", e.getCause(), e.getMessage());
            return false;
        }

        return true;
    }

    public Long getTokenExpireTime(String jwt) {

        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
            log.info("[expireTime] = {}", claims.getExpiration());
            log.info("[subject] = {}", claims.getSubject());

            return claims.getExpiration().getTime();
        } catch (JwtException | NullPointerException e) {
            log.error("token error!!");
            throw new JwtException("token error!!");
        }
    }

    public Long mapTokenToId(String token) {

        String s = tokenPayloadExtract(token);

        return Long.parseLong(s);
    }
}
