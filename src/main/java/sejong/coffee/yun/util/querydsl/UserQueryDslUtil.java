package sejong.coffee.yun.util.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import sejong.coffee.yun.domain.user.UserRank;

import java.util.function.Supplier;

import static sejong.coffee.yun.domain.user.QMember.member;

public class UserQueryDslUtil {

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> booleanExpressionSupplier) {
        try {
            return new BooleanBuilder(booleanExpressionSupplier.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }

    public static BooleanBuilder updateCheckOrderCountAndUserRank(int orderCount, UserRank userRank) {
        return nullSafeBuilder(() ->
                member.orderCount.between(orderCount, orderCount + 4))
                .and(member.userRank.eq(userRank)
                );
    }

    public static BooleanBuilder checkUserRank(UserRank userRank) {
        return nullSafeBuilder(() -> member.userRank.eq(userRank));
    }
}
