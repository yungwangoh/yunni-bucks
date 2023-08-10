package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final Calculator calculator;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Order order(Long memberId, List<Menu> menuList) {

        Member member = userRepository.findById(memberId);

        Money money = calculator.calculateMenus(member, menuList);

        member.addOrderCount();

        Order order = Order.createOrder(member, menuList, money);

        return orderRepository.save(order);
    }

    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId);

        order.cancel();
    }

    public Order findOrderByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public Order updateAddMenu(Long memberId, Long menuId) {
        Order order = orderRepository.findByMemberId(memberId);

        if(order.getStatus() == OrderStatus.ORDER && order.getPayStatus() == OrderPayStatus.NO) {
            Menu menu = menuRepository.findById(menuId);

            order.addMenu(menu);

            Money money = calculator.calculateMenus(order.getMember(), order.getMenuList());

            order.updatePrice(money);

        } else {
            throw new RuntimeException("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다.");
        }

        return order;
    }

    @Transactional
    public Order updateRemoveMenu(Long memberId, int menuIdx) {
        Order order = orderRepository.findByMemberId(memberId);

        if(order.getStatus() == OrderStatus.ORDER && order.getPayStatus() == OrderPayStatus.NO) {
            order.removeMenu(menuIdx);

            Money money = calculator.calculateMenus(order.getMember(), order.getMenuList());

            order.updatePrice(money);

        } else {
            throw new RuntimeException("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다.");
        }

        return order;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
