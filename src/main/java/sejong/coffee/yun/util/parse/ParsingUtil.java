package sejong.coffee.yun.util.parse;

import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.util.regex.RegexUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class ParsingUtil {

    /**
     * String Type 날짜를 "/"를 기준으로 분리한다.(MM/YY) (ex. 11/23 -> 11 23)
     *
     * @param validThru
     * @return
     */
    public static String[] parsingCardValidDate(String validThru) {
        return validThru.split("/");
    }

    public static String parsingMemberIdentityNumber(String identityNumber) {
        return identityNumber.split("@")[0];
    }

    public static String parsingFileExtension(String filePath) {

        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < filePath.length() - 1 && filePath.matches(RegexUtil.IMG_FORMAT)) {
            return filePath.substring(lastDotIndex + 1);
        }
        throw ExceptionControl.INVALID_FILE_EXTENSION_FORMAT.ocrException();
    }

    public static String parsingDataTimePattern() {
        // 현재 날짜와 시간을 얻기
        Date currentDate = new Date();

        // 날짜 포맷을 "yyyyMMddHHmmss"로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        // 날짜를 지정한 포맷에 맞게 문자열로 변환
        return dateFormat.format(currentDate);
    }

    public static LocalDateTime parsingISO8601ToLocalDateTime(String approvedAt) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(approvedAt, formatter);
            return offsetDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(approvedAt);
        }
    }
}
