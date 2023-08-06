package sejong.coffee.yun.util.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public class PasswordUtil {

    private static final String SALT = salt();

    private static String salt() {

        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[20];

        sr.nextBytes(salt);

        return getToHex(salt);
    }

    private static String getToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for(var a : bytes) {
            sb.append(String.format("%02x", a));
        }

        return sb.toString();
    }

    public static String encryptPassword(String password) {

        String str = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update((password + SALT).getBytes());
            byte[] saltedPassword = md.digest();

            str = getToHex(saltedPassword);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static boolean match(String rawString, String string) {
        String password = PasswordUtil.encryptPassword(string);

        return Objects.equals(rawString, password);
    }
}
