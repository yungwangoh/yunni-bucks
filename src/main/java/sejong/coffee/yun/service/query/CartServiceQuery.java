package sejong.coffee.yun.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.service.CartService;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceQuery implements CartService {

    private final CartRepository cartRepository;

    @Override
    public Menu getMenu(Long memberId, int idx) {
        Cart cart = cartRepository.findByMember(memberId);

        return cart.getMenu(idx);
    }
    @Override
    public Cart findCartByMember(Long memberId) {
        return cartRepository.findByMember(memberId);
    }
}
