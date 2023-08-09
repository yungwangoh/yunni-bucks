package sejong.coffee.yun.repository.card.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.card.jpa.JpaCardRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepository {

    private final JpaCardRepository jpaCardRepository;

    @Override
    public Card save(Card card) {
        return jpaCardRepository.save(card);
    }

    @Override
    public Optional<Card> findById(Long id) {
        return jpaCardRepository.findById(id);
    }

    @Override
    public Optional<Card> findByMemberId(Long memberId) {
        return jpaCardRepository.findById(memberId);
    }

    @Override
    public List<Card> findAll() {
        return jpaCardRepository.findAll();
    }
}
