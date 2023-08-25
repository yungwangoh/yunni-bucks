package sejong.coffee.yun.repository.cart;

import sejong.coffee.yun.domain.user.Cart;

public interface CartRepository {

    Cart save(Cart cart);
    boolean existByMemberId(Long memberId);
    Cart findById(Long id);
    Cart findByMember(Long memberId);
    void delete(Long id);
    void delete(Cart cart);
    void clear();
}
