package sejong.coffee.yun.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.repository.card.CardRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Builder
public class CardService {

    private final CardRepository cardRepository;

    public Optional<Card> findById(long id) {
        return cardRepository.findById(id);
    }

    public Card getByMemberId(long memberId) {
        return cardRepository.findByMemberId(memberId)
                .orElseThrow(ExceptionControl.NOT_FOUND_REGISTER_CARD::cardException);
    }
}
