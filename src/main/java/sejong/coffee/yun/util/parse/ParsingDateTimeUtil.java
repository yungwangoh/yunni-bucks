package sejong.coffee.yun.util.parse;

public class ParsingDateTimeUtil {

    /**
     * String Type 날짜를 "/"를 기준으로 분리한다. (ex. 23/11 -> 23 11)
     * @param validThru
     * @return
     */
    public static String[] parsingCardValidDate(String validThru) {
        return validThru.split("/");
    }
}
