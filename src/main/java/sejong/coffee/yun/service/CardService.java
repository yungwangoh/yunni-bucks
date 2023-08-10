package sejong.coffee.yun.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.repository.card.CardRepository;

@Service
@RequiredArgsConstructor
@Builder
public class CardService {

    private final CardRepository cardRepository;

    public Card findById(long id) {
        return cardRepository.findById(id);
    }

    public Card getByMemberId(long memberId) {
        return cardRepository.findByMemberId(memberId);
    }
}
