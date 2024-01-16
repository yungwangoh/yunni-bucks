package sejong.coffee.yun.domain.user;

import lombok.Getter;
import sejong.coffee.yun.domain.discount.type.DiscountType;

@Getter
public enum UserRank implements DiscountType {

    BRONZE(0.0) {
        @Override
        public double getDiscountRate() {
            return super.getDiscountRate();
        }
    },
    SILVER(0.1) {
        @Override
        public double getDiscountRate() {
            return super.getDiscountRate();
        }
    },
    GOLD(0.15) {
        @Override
        public double getDiscountRate() {
            return super.getDiscountRate();
        }
    },
    PLATINUM(0.2) {
        @Override
        public double getDiscountRate() {
            return super.getDiscountRate();
        }
    },
    DIAMOND(0.3) {
        @Override
        public double getDiscountRate() {
            return super.getDiscountRate();
        }
    };

    private final double discountRate;

    UserRank(double discountRate) {
        this.discountRate = discountRate;
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

    public UserRank downRank() {

        switch (this) {
            case SILVER -> {return BRONZE;}
            case GOLD -> {return SILVER;}
            case PLATINUM -> {return GOLD;}
            case DIAMOND -> {return PLATINUM;}
        }

        return null;
    }

    public UserRank upRank() {

        switch (this) {
            case BRONZE -> {return SILVER;}
            case SILVER -> {return GOLD;}
            case GOLD -> {return PLATINUM;}
            case PLATINUM -> {return DIAMOND;}
        }
        
        return null;
    }
}
