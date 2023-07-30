package sejong.coffee.yun.util.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public class PasswordUtil {

    private static String salt() {

        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[20];

        sr.nextBytes(salt);

        StringBuilder sb = new StringBuilder();

        for(var a : salt) {
            sb.append(String.format("%02x", a));
        }

        return sb.toString();
    }

    public static String encryptPassword(String password) {

        String str = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update((password + salt()).getBytes());
            byte[] saltedPassword = md.digest();

            StringBuilder sb = new StringBuilder();

            for(var a : saltedPassword) {
                sb.append(String.format("%02x", a));
            }

            str = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static boolean match(String rawString, String string) {
        return Objects.equals(rawString, PasswordUtil.encryptPassword(string));
    }
}
