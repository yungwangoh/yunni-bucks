package sejong.coffee.yun.repository.card.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sejong.coffee.yun.domain.user.Card;

public interface JpaCardRepository extends JpaRepository<Card, Long> {

    // 카드 FK로 회원 찾기
    @Query("select c from Card c where Card.member.id =: memberId")
    Card findByMemberId(Long memberId);
}
