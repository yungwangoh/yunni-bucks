package sejong.coffee.yun.controller.mock;

import lombok.Builder;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.mock.repository.FakeCardRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CardService;

public class TestCardContainer {

    public final CardService cardService;
    public final CardRepository cardRepository;
    public final UserRepository userRepository;

    @Builder
    public TestCardContainer() {
        this.cardRepository = new FakeCardRepository();
        this.userRepository = new FakeUserRepository();
        this.cardService = new CardService(this.cardRepository, this.userRepository);
    }
}
