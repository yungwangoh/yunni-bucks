package sejong.coffee.yun.repository.cart.fake;

import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.repository.cart.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;


@Repository
public class FakeCartRepository implements CartRepository {

    private final List<Cart> carts = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Cart save(Cart cart) {
        Cart newCart = Cart.from(++id, cart);

        carts.add(newCart);

        return newCart;
    }

    @Override
    public Cart findById(Long id) {
        return carts.stream()
                .filter(cart -> Objects.equals(cart.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_CART::notFoundException);
    }

    @Override
    public Cart findByMember(Long memberId) {
        return carts.stream()
                .filter(cart -> Objects.equals(cart.getMember().getId(), memberId))
                .findAny()
                .orElseThrow(NOT_FOUND_CART::notFoundException);
    }

    @Override
    public void delete(Long id) {
        Cart findCart = carts.stream()
                .filter(cart -> Objects.equals(cart.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_CART::notFoundException);

        carts.remove(findCart);
    }

    @Override
    public void delete(Cart cart) {
        carts.remove(cart);
    }
}
