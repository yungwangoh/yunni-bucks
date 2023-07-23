package sejong.coffee.yun.domain.user;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Money {

    public static Money ZERO = Money.initialPrice(new BigDecimal(0));
    private BigDecimal totalPrice;

    private Money(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void plus(Money money) {
        this.totalPrice = this.totalPrice.add(money.totalPrice);
    }

    public void minus(Money money) {
        this.totalPrice = this.totalPrice.subtract(money.totalPrice);

        if(this.totalPrice.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("잘못된 요금 계산입니다.");
        }
    }

    public void discount(BigDecimal discount) {
        if(discount.compareTo(new BigDecimal("1")) > 0 || discount.compareTo(new BigDecimal("0")) < 0) {
            throw new IllegalArgumentException("할인률은 1 이하 0이상이여야 합니다.");
        }

        BigDecimal mul = this.totalPrice.multiply(discount);

        this.totalPrice = this.totalPrice.add(mul);
    }

    public static Money initialPrice(BigDecimal totalPrice) {
        return new Money(totalPrice);
    }
}
