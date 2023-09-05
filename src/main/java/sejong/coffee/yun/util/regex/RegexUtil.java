package sejong.coffee.yun.util.regex;

public interface RegexUtil {
    String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";
    String NAME = "^[가-힣]{2,10}$";
    String IMG_FORMAT = "^.*\\.(jpg|jpeg|png|pdf)$";
    String CARD_NUMBER = "(?=.*[0-9]).{1,20}";
    String CARD_PASSWORD = "(?=.*[0-9]).{4}";
    String CARD_VALID_THRU = "\"\\\\d{2}/\\\\d{2}\"";
}
