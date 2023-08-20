package sejong.coffee.yun.service.card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.pay.BeforeCreatedData;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.card.CardDto;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.card.fake.FakeCardRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CardService;

import static org.assertj.core.api.Assertions.assertThat;

public class CardServiceTest extends BeforeCreatedData {

    private CardService cardService;
    private final UserRepository userRepository = new FakeUserRepository();

    @BeforeEach
    void init() {

        this.cardService = CardService.builder()
                .cardRepository(new FakeCardRepository())
                .userRepository(userRepository)
                .build();
        userRepository.save(this.member);
    }

    @Test
    void 카드를_저장한다() {
        //given
        Member findMember = userRepository.findByEmail(this.member.getEmail());

        Card buildCard = Card.builder()
                .member(findMember)
                .number(this.card.getNumber())
                .cardPassword(this.card.getCardPassword())
                .validThru(this.card.getValidThru())
                .build();

        //when
        Card card = cardService.create(findMember.getId(), new CardDto.Request(buildCard.getNumber(), buildCard.getCardPassword(), buildCard.getValidThru()));

        //then
        assertThat(buildCard.getCardPassword()).isEqualTo(card.getCardPassword());
        assertThat(buildCard.getValidThru()).isEqualTo(card.getValidThru());
    }

    @Test
    void findById는_카드_단건_조회를_한다() {

        //given
        Member findMember = userRepository.findByEmail(this.member.getEmail());
        Card buildCard = Card.builder()
                .member(findMember)
                .number(this.card.getNumber())
                .cardPassword(this.card.getCardPassword())
                .validThru(this.card.getValidThru())
                .build();

        Card card = cardService.create(findMember.getId(), new CardDto.Request(buildCard.getNumber(), buildCard.getCardPassword(), buildCard.getValidThru()));

        //when
        Card findCard = cardService.findById(card.getId());

        //then
        assertThat(card.getCardPassword()).isEqualTo(findCard.getCardPassword());
        assertThat(card.getValidThru()).isEqualTo(findCard.getValidThru());
    }

    @Test
    void getByMemberId는_멤버id로_카드_조회를_한다() {

        //given
        Member findMember = userRepository.findByEmail(this.member.getEmail());
        Card buildCard = Card.builder()
                .member(findMember)
                .number(this.card.getNumber())
                .cardPassword(this.card.getCardPassword())
                .validThru(this.card.getValidThru())
                .build();

        Card card = cardService.create(findMember.getId(), new CardDto.Request(buildCard.getNumber(), buildCard.getCardPassword(), buildCard.getValidThru()));

        //when
        Card findCard = cardService.getByMemberId(card.getMember().getId());

        //then
        assertThat(card.getMember().getName()).isEqualTo(findCard.getMember().getName());
        assertThat(card.getNumber()).isEqualTo(findCard.getNumber());
    }
}
