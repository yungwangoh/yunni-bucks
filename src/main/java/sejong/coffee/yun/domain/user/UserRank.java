package sejong.coffee.yun.domain.user;

public enum UserRank {

    BRONZE, SILVER, GOLD, PLATINUM, DIAMOND;

    UserRank() {
    }

    public static UserRank calculateUserRank(int orderCount) {

        if(checkCondition(orderCount,1, 5)) {
            return UserRank.SILVER;
        } else if (checkCondition(orderCount,6, 10)) {
            return UserRank.GOLD;
        } else if (checkCondition(orderCount,11, 15)) {
            return UserRank.PLATINUM;
        } else if (checkCondition(orderCount, 16)) {
            return UserRank.DIAMOND;
        }

        return UserRank.BRONZE;
    }

    private static boolean checkCondition(long totalMonths, long start) {
        return totalMonths >= start;
    }

    private static boolean checkCondition(int orderCount, int start, int end) {
        return (orderCount >= start && orderCount <= end);
    }
}
