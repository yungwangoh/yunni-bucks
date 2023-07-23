package sejong.coffee.yun.domain.order;

import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Money;

import java.util.List;

public class Calculator {

    public Money calculateMenus(List<Menu> menus) {
        Money initialPrice = Money.ZERO;

        menus.forEach(menu -> initialPrice.plus(menu.getPrice()));

        return initialPrice;
    }
}
