package sejong.coffee.yun.repository.cart.fake;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.repository.cart.CartRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FakeCartRepository implements CartRepository {

    private final List<Cart> carts;

    @Override
    public Cart save(Cart cart) {
        return null;
    }

    @Override
    public Cart findById(Long id) {
        return null;
    }

    @Override
    public Cart findByMember(Long memberId) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
