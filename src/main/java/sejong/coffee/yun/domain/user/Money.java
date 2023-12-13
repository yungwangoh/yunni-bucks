package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    public static Money ZERO = Money.initialPrice(BigDecimal.ZERO);
    private BigDecimal totalPrice;

    public Money(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Money plus(Money money) {
        return new Money(this.totalPrice.add(money.totalPrice));
    }

    public Money minus(Money money) {
        BigDecimal subtract = this.totalPrice.subtract(money.totalPrice);

        if(subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("잘못된 요금 계산입니다.");
        }

        return new Money(subtract);
    }

    public Money discount(BigDecimal discount) {
        if(discount.compareTo(new BigDecimal("1")) > 0 || discount.compareTo(new BigDecimal("0")) < 0) {
            throw new IllegalArgumentException("할인률은 1 이하 0이상이여야 합니다.");
        }

        BigDecimal mul = this.totalPrice.multiply(discount);

        return new Money(this.totalPrice.subtract(mul));
    }

    public Money mapBigDecimalToInt() {
        return new Money(BigDecimal.valueOf(this.totalPrice.intValue()));
    }

    public Money mapBigDecimalToLong() {
        return new Money(BigDecimal.valueOf(this.totalPrice.longValue()));
    }

    public static Money initialPrice(BigDecimal totalPrice) {
        return new Money(totalPrice);
    }

    public int mapToInt() {
        return this.getTotalPrice().intValue();
    }
}
