package sejong.coffee.yun.domain.order;

import sejong.coffee.yun.domain.DateTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Order extends DateTimeEntity {

    @Id @GeneratedValue
    private Long id;
}
