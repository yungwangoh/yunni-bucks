package sejong.coffee.yun.domain.order;

import sejong.coffee.yun.domain.order.menu.Menu;

import javax.persistence.*;

@Entity
public class MenuList {

    @Id @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;
}
