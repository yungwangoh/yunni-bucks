package sejong.coffee.yun.repository.card.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.user.Card;

import java.util.Optional;

public interface JpaCardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByMemberId(Long memberId);
}
