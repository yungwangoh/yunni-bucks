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

        if(checkEmptyMenuList(menus.size())) return Money.ZERO;

        double discount = discountPolicy.calculateDiscount(member);

        Money money = menus.stream().map(Menu::getPrice).reduce(Money::plus).orElse(Money.ZERO);

        Money discountMoney = money.discount(BigDecimal.valueOf(discount));

        return discountMoney.mapBigDecimalToLong();
    }

    private boolean checkEmptyMenuList(int size) {
        return size <= 0;
    }
}
