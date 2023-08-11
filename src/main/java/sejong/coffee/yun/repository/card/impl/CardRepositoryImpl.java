package sejong.coffee.yun.repository.card.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.card.jpa.JpaCardRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepository {

    private final JpaCardRepository jpaCardRepository;

    @Override
    public Card save(Card card) {
        return jpaCardRepository.save(card);
    }

    @Override
    public Card findById(Long id) {
        return jpaCardRepository.findById(id)
                .orElseThrow(ExceptionControl.NOT_FOUND_REGISTER_CARD::cardException);
    }

    @Override
    public Card findByMemberId(Long memberId) {
        return jpaCardRepository.findById(memberId)
                .orElseThrow(ExceptionControl.NOT_FOUND_REGISTER_CARD::cardException);
    }

    @Override
    public List<Card> findAll() {
        return jpaCardRepository.findAll();
    }
}
