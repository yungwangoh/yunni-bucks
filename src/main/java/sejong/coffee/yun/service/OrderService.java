package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.MenuList;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final Calculator calculator;
    private final UserRepository userRepository;

    @Transactional
    public Order order(Long memberId, MenuList menuList) {

        Member member = userRepository.findById(memberId);

        Money money = calculator.calculateMenus(member, menuList.getMenus());

        member.addOrderCount();

        Order order = Order.createOrder(member, menuList, money);

        return orderRepository.save(order);
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
