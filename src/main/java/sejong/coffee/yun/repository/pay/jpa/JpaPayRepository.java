package sejong.coffee.yun.repository.pay.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sejong.coffee.yun.domain.pay.CardPayment;

public interface JpaPayRepository extends JpaRepository<CardPayment, Long> {

    @Query("select c from Card c left join c.member where c.member.id =: memberId")
    void findCardByOrderWithinMember(@Param("memberId") Long id);
}
