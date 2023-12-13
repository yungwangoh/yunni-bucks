package sejong.coffee.yun.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.UserService;

@Component
@RequiredArgsConstructor
public class UserServiceFacade {

    private final UserService userService;
    private final CartService cartService;

    @Transactional
    public Member signUp(String name, String email, String password, Address address) {
        Member member = userService.signUp(name, email, password, address);
        Cart cart = cartService.createCart(member.getId());

        return cart.getMember();
    }
}
