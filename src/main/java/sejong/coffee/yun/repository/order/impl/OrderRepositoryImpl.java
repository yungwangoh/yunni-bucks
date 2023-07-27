package sejong.coffee.yun.repository.order.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.order.jpa.JpaOrderRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    @Transactional
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
       return jpaOrderRepository.findById(id)
               .orElseThrow(NOT_FOUND_ORDER::notFoundOrderException);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll();
    }
}
