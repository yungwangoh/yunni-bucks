package sejong.coffee.yun.repository.cart.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.user.Cart;

import java.util.Optional;

public interface JpaCartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
}
