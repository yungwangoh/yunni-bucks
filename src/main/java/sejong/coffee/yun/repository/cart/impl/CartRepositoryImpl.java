package sejong.coffee.yun.repository.cart.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.cart.jpa.JpaCartRepository;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;

@Repository
@Primary
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final JpaCartRepository jpaCartRepository;

    @Override
    public Cart save(Cart cart) {
        return jpaCartRepository.save(cart);
    }

    @Override
    public Cart findById(Long id) {
        return jpaCartRepository.findById(id)
                .orElseThrow(NOT_FOUND_CART::notFoundException);
    }

    @Override
    public Cart findByMember(Long memberId) {
        return jpaCartRepository.findByMemberId(memberId)
                .orElseThrow(NOT_FOUND_CART::notFoundException);
    }

    @Override
    public void delete(Long id) {
        jpaCartRepository.deleteById(id);
    }

    @Override
    public void delete(Cart cart) {
        jpaCartRepository.delete(cart);
    }
}
