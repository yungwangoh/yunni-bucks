package sejong.coffee.yun.service.read;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Builder
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public Card findById(Long id) {
        return cardRepository.findById(id);
    }

    public Card getByMemberId(Long memberId) {
        return cardRepository.findByMemberId(memberId);
    }
}
