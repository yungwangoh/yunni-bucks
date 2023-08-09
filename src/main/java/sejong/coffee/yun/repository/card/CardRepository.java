package sejong.coffee.yun.repository.card;

import sejong.coffee.yun.domain.user.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepository {

    Card save(Card card);

    Optional<Card> findById(Long id);
    Optional<Card> findByMemberId(Long memberId);

    List<Card> findAll();
}
