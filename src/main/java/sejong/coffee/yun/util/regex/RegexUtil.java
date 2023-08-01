package sejong.coffee.yun.util.regex;

public interface RegexUtil {
    String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";
    String NAME = "^[가-힣]{2, 10}$";
}
