package sejong.coffee.yun.domain.order;

import lombok.RequiredArgsConstructor;
import sejong.coffee.yun.domain.discount.policy.DiscountPolicy;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.exception.MenuException;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.User;

import java.math.BigDecimal;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

@RequiredArgsConstructor
public class Calculator {

    private final DiscountPolicy discountPolicy;

    public Money calculateMenus(User user, List<Menu> menus) {

        checkEmptyMenuList(menus.size());

        double discount = discountPolicy.calculateDiscount(user);

        Money initialPrice = Money.initialPrice(new BigDecimal(0));

        menus.forEach(menu -> initialPrice.plus(menu.getPrice()));

        initialPrice.discount(BigDecimal.valueOf(discount));

        return initialPrice;
    }

    private void checkEmptyMenuList(int size) {
        if(size <= 0) throw EMPTY_MENUS.throwException();
    }
}
