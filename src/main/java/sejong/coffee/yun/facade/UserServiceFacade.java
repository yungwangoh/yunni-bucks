package sejong.coffee.yun.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.service.command.CartServiceCommand;
import sejong.coffee.yun.service.command.UserServiceCommand;

@Component
@RequiredArgsConstructor
public class UserServiceFacade {

    private final UserServiceCommand userService;
    private final CartServiceCommand cartService;

    @Transactional
    public Member signUp(String name, String email, String password, Address address) {
        Member member = userService.signUp(name, email, password, address);
        Cart cart = cartService.createCart(member.getId());

        return cart.getMember();
    }

    @Transactional
    public void withdrawUser(Long memberId) {
        cartService.removeCart(memberId);
        userService.deleteMember(memberId);
    }
}
