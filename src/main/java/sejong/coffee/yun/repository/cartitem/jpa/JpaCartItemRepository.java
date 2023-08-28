package sejong.coffee.yun.repository.cartitem.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.user.CartItem;

public interface JpaCartItemRepository extends JpaRepository<CartItem, Long> {
}
