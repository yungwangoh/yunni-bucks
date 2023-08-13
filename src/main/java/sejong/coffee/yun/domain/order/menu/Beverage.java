package sejong.coffee.yun.domain.order.menu;

import sejong.coffee.yun.domain.user.Money;

import java.time.LocalDateTime;

public class Beverage extends Menu {

    public Beverage(final String title, final String description,
                    final Money price, final Nutrients nutrients,
                    final MenuSize menuSize, final LocalDateTime now) {

        super(title, description, price, nutrients, menuSize, now);
    }
}

