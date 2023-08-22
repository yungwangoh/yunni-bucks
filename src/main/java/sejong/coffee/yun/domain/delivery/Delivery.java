package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
public abstract class Delivery {

    @Id @GeneratedValue
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    private Address address;
    @Enumerated(value = EnumType.STRING)
    private DeliveryType type;
    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus status;

    protected Delivery(Long id, Order order, LocalDateTime now, Address address, DeliveryType type, DeliveryStatus status) {
        this.id = id;
        this.order = order;
        this.createAt = now;
        this.updateAt = now;
        this.address = address;
        this.type = type;
        this.status = status;
    }

    public void updateAddress(Address address, LocalDateTime now) {
        this.address = address;
        this.updateAt = now;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public abstract void cancel();
    public abstract void delivery();
    public abstract void complete();
}
