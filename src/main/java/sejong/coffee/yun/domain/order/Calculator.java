package sejong.coffee.yun.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.discount.policy.DiscountPolicy;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Calculator {

    private final DiscountPolicy discountPolicy;

    public Money calculateMenus(Member member, List<Menu> menus) {

        Money initialPrice = Money.initialPrice(new BigDecimal(0));

        if(checkEmptyMenuList(menus.size())) return initialPrice;

        double discount = discountPolicy.calculateDiscount(member);

        menus.forEach(menu -> initialPrice.plus(menu.getPrice()));

        initialPrice.discount(BigDecimal.valueOf(discount));

        initialPrice.mapBigDecimalToLong();

        return initialPrice;
    }

    private boolean checkEmptyMenuList(int size) {
        return size <= 0;
    }
}
