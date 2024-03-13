package sejong.coffee.yun.repository.cartitem.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.CartItem;
import sejong.coffee.yun.dto.cart.CartDto;
import sejong.coffee.yun.repository.cartitem.CartItemRepository;
import sejong.coffee.yun.repository.cartitem.jpa.JpaCartItemRepository;

import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.user.QCartItem.cartItem;

@Repository
@Primary
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepository {

    private final JpaCartItemRepository jpaCartItemRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CartItem save(CartItem cartItem) {
        return jpaCartItemRepository.save(cartItem);
    }

    @Override
    public void clear() {
        jpaCartItemRepository.deleteAll();
    }

    @Override
    public List<CartDto.StockRecord> stockRecord(Long cartId) {
        List<Tuple> tuples = jpaQueryFactory.select(cartItem.menu.id, cartItem.menu.count())
                .from(cartItem)
                .where(cartItem.cart.id.eq(cartId))
                .groupBy(cartItem.menu, cartItem.cart)
                .fetch();

        return tuples.stream().map(tuple ->
                new CartDto.StockRecord(
                        Objects.requireNonNull(tuple.get(cartItem.menu.id)),
                        tuple.get(cartItem.menu.count())
                )).toList();
    }
}
