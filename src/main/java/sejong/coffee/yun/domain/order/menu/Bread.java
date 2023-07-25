package sejong.coffee.yun.domain.order.menu;

import sejong.coffee.yun.domain.user.Money;

public class Bread extends Menu {

    public Bread(final String title, final String description, final Money price, final Nutrients nutrients, final MenuSize menuSize) {
        super(title, description, price, nutrients, menuSize);
    }
}
