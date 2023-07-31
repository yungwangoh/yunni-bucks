package sejong.coffee.yun.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.menu.Menu;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuList {

    @Id @GeneratedValue
    private Long id;
    @OneToMany
    private List<Menu> menus = new ArrayList<>();

    public MenuList(List<Menu> menus) {
        this.menus = menus;
    }

    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }

    public void removeMenuBy(Menu menu) {
        this.menus.remove(menu);
    }

    public void removeMenuBy(int index) {
        this.menus.remove(index);
    }

    public Menu getMenuBy(int index) {
        return this.menus.get(index);
    }
}
