package sejong.coffee.yun.util.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.StringTokenizer;
import java.util.function.Supplier;

import static sejong.coffee.yun.domain.order.menu.QMenuReview.menuReview;

public class MenuReviewQueryDslUtil {

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> booleanExpressionSupplier) {
        try {
            return new BooleanBuilder(booleanExpressionSupplier.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }

    public static BooleanBuilder containCheckStrings(StringTokenizer st) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        for(int i = 0; i < st.countTokens(); i++) {
            booleanBuilder.or(menuReview.comments.containsIgnoreCase(st.nextToken()));
        }

        return booleanBuilder;
    }
}
