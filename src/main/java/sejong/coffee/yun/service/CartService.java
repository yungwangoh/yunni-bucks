package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.repository.cart.CartRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public void createCart(Member member) {
        Cart cart = new Cart(member, new ArrayList<>());

        cartRepository.save(cart);
    }

    public Cart findCartByMember(Long memberId) {
        return cartRepository.findByMember(memberId);
    }

    public List<Menu> findMenusByMember(Long memberId) {
        Cart cart = cartRepository.findByMember(memberId);

        return cart.getMenuList();
    }

    @Transactional
    public void addMenu(Long cartId, Menu menu) {
        Cart cart = cartRepository.findById(cartId);

        cart.addMenu(menu);
    }

    public Menu getMenu(Long cartId, int idx) {
        Cart cart = cartRepository.findById(cartId);

        return cart.getMenu(idx);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId);

        cart.clearMenuList();
    }

    @Transactional
    public void removeMenu(Long cartId, int idx) {
        Cart cart = cartRepository.findById(cartId);

        cart.removeMenu(idx);
    }

    @Transactional
    public void removeCart(Long cartId) {
        cartRepository.delete(cartId);
    }
}
