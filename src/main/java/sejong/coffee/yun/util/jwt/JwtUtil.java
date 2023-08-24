package sejong.coffee.yun.util.jwt;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class JwtUtil {


    public static String getFormatToken(String jwt) {
        // bearer
        return jwt.split(" ")[1];
    }

    public static String getSigningKey(String key) {
        return Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }

    public static String getBearerToken(String token) {
        return "bearer " + token;
    }
}
