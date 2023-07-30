package sejong.coffee.yun.repository.pay;

import sejong.coffee.yun.domain.pay.CardPayment;

import java.util.List;

public interface PayRepository {

    CardPayment save(CardPayment cardPayment);

    CardPayment findById(Long id);

    List<CardPayment> findAll();

}
