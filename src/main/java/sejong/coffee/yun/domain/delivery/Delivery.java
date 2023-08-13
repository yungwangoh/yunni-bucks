package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.DO_NOT_PAID;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private DeliveryStatus status;

    @Builder
    public Delivery(Order order, LocalDateTime createAt, LocalDateTime updateAt, Address address, DeliveryStatus status) {
        this.order = order;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.address = address;
        this.status = status;
    }

    public abstract Delivery delivery();

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void checkOrderStatus() {
        if(!Objects.equals(this.order.getPayStatus(), OrderPayStatus.YES)) {
            throw new IllegalArgumentException(DO_NOT_PAID.getMessage());
        }
    }
}
