package sejong.coffee.yun.repository.card.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.user.Card;

public interface JpaCardRepository extends JpaRepository<Card, Long> {

    Card findByMemberId(Long memberId);
}
