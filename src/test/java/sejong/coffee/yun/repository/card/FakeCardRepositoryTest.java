package sejong.coffee.yun.repository.card;


import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.pay.BeforeCreatedData;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.mock.repository.FakeCardRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeCardRepositoryTest extends BeforeCreatedData {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public FakeCardRepositoryTest() {
        this.cardRepository = new FakeCardRepository();
        this.userRepository = new FakeUserRepository();
    }

    @Test
    void 카드를_저장한다() {
        //given
        //when
        Card save = cardRepository.save(this.card);
        //then
        assertThat(card.getCardPassword()).isEqualTo(save.getCardPassword());
        assertThat(card.getValidThru()).isEqualTo(save.getValidThru());
    }

    @Test
    void 회원_id로_카드를_조회한다() {
        //given
        Member member = userRepository.save(this.member);
        Card buildCard = Card.builder()
                .member(member)
                .number(this.card.getNumber())
                .cardPassword(this.card.getCardPassword())
                .validThru(this.card.getValidThru())
                .build();

        buildCard = cardRepository.save(buildCard);

        //when
        Member findMember = userRepository.findById(member.getId());
        Card findCard = cardRepository.findByMemberId(findMember.getId());

        //then
        assertThat(buildCard.getMember().getName()).isEqualTo(findCard.getMember().getName());
        assertThat(buildCard.getNumber()).isEqualTo(findCard.getNumber());
        assertThat(buildCard.getCardPassword()).isEqualTo(findCard.getCardPassword());
    }
}
