package sejong.coffee.yun.repository.cart;

import sejong.coffee.yun.domain.user.Cart;

public interface CartRepository {

    Cart save(Cart cart);
    Cart findById(Long id);
    Cart findByMember(Long memberId);
    void delete(Long id);
}
