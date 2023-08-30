package sejong.coffee.yun.repository.cart.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sejong.coffee.yun.domain.user.Cart;

import java.util.Optional;

public interface JpaCartRepository extends JpaRepository<Cart, Long> {
    @Query("select c from Cart c join fetch c.member m where m.id = :memberId")
    Optional<Cart> findByMemberId(@Param("memberId") Long memberId);
    boolean existsByMemberId(Long memberId);
}
