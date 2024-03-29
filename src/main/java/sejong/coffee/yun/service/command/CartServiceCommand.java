package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.CartItem;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.cartitem.CartItemRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.ArrayList;

import static sejong.coffee.yun.domain.exception.ExceptionControl.DUPLICATE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartServiceCommand {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final CartItemRepository cartItemRepository;

    public Cart createCart(Long memberId) {

        if(cartRepository.existByMemberId(memberId)) throw DUPLICATE.duplicatedException();

        Member member = userRepository.findById(memberId);

        Cart cart = Cart.builder()
                .member(member)
                .cartItems(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }

    public Cart addMenu(Long memberId, Long menuId) {
        Menu menu = menuRepository.findById(menuId);
        Cart cart = cartRepository.findByMember(memberId);

        CartItem saveCartItem = cartItemRepository.save(
                CartItem.builder()
                .cart(cart)
                .menu(menu)
                .build()
        );

        cart.addMenu(saveCartItem);

        cart.getCartItems().forEach(CartItem::getId);

        return cart;
    }

    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId);

        cart.clearCartItems();
    }

    public Cart removeMenu(Long memberId, int idx) {
        Cart cart = cartRepository.findByMember(memberId);

        cart.removeMenu(idx);

        return cart;
    }

    public void removeCart(Long memberId) {
        try {
            Cart cart = cartRepository.findByMember(memberId);
            cartRepository.delete(cart);
        } catch (NotFoundException e) {
            log.info("empty cart = {}", e.getMessage());
        }
    }
}
