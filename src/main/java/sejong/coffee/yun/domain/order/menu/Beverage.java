package sejong.coffee.yun.domain.order.menu;

import sejong.coffee.yun.domain.user.Money;

public final class Beverage extends Menu {

    public Beverage(String title, String description, Money price, Nutrients nutrients, MenuSize menuSize) {
        super(title, description, price, nutrients, menuSize);
    }
}

