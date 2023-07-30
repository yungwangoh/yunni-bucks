package sejong.coffee.yun.repository.pay.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.repository.pay.PayRepository;
import sejong.coffee.yun.repository.pay.jpa.JpaPayRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PayRepositoryImpl implements PayRepository {

    private final JpaPayRepository jpaPayRepository;

    @Override
    public CardPayment save(CardPayment cardPayment) {
        return jpaPayRepository.save(cardPayment);
    }

    @Override
    public CardPayment findById(Long id) {
        return jpaPayRepository.findById(id)
                .orElseThrow(ExceptionControl.EMPTY_MENUS::notFoundOrderException);
    }

    @Override
    public List<CardPayment> findAll() {
        return jpaPayRepository.findAll();
    }
}
