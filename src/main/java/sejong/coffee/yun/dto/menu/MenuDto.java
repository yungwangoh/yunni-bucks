package sejong.coffee.yun.dto.menu;

import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Money;

import java.time.LocalDateTime;

public class MenuDto {

    public record Request(String title, String description, Money price, Nutrients nutrients, MenuSize menuSize, LocalDateTime now, int quantity) {}
    public record Response(Long id, String title, String description, Money price, Nutrients nutrients, MenuSize menuSize) {
        public Response(Menu menu) {
            this(menu.getId(), menu.getTitle(), menu.getDescription(), menu.getPrice().mapBigDecimalToInt(), menu.getNutrients(), menu.getMenuSize());
        }
    }
}
