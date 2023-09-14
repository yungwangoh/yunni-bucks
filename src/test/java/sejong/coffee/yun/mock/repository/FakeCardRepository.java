package sejong.coffee.yun.mock.repository;

import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.repository.card.CardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_REGISTER_CARD;

public class FakeCardRepository implements CardRepository {

    private final AtomicLong atomicGeneratedId = new AtomicLong(0);
    private final List<Card> data = new ArrayList<>();

    @Override
    public Card save(Card card) {
        if (card.getId() == null || card.getId() == 0L) {
            Card buildCard = Card.builder()
                    .id(atomicGeneratedId.incrementAndGet())
                    .number(card.getNumber())
                    .cardPassword(card.getCardPassword())
                    .member(card.getMember())
                    .validThru(card.getValidThru())
                    .member(card.getMember())
                    .build();
            data.add(buildCard);
            return buildCard;
        }
        data.removeIf(element -> Objects.equals(element.getId(), card.getId()));
        data.add(card);
        return card;
    }

    @Override
    public Card findById(Long id) {
        return data.stream().filter(element -> Objects.equals(element.getId(), id)).findAny()
                .orElseThrow(NOT_FOUND_REGISTER_CARD::cardException);
    }

    @Override
    public List<Card> findAll() {
        return data;
    }

    @Override
    public Card findByMemberId(Long memberId) {
        return data.stream().filter(element -> Objects.equals(element.getMember().getId(), memberId)).findAny()
                .orElseThrow(NOT_FOUND_REGISTER_CARD::cardException);
    }

    @Override
    public void delete(Long id) {
        data.removeIf(element -> Objects.equals(element.getId(), id));
    }

    @Override
    public void delete(Card card) {
        data.removeIf(element -> Objects.equals(element.getId(), card.getId()));
    }

    @Override
    public void clear() {
        data.clear();
    }
}
