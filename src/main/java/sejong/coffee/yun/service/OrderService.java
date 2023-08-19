package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final Calculator calculator;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Order order(Long memberId, List<Menu> menuList, LocalDateTime now) {

        Member member = userRepository.findById(memberId);

        Money money = calculator.calculateMenus(member, menuList);

        member.addOrderCount();

        Order order = Order.createOrder(member, menuList, money, now);

        return orderRepository.save(order);
    }

    @Transactional
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
    public Order updateAddMenu(Long memberId, Long menuId, LocalDateTime now) {
        Order order = orderRepository.findByMemberId(memberId);

        if(order.getStatus() == OrderStatus.ORDER && order.getPayStatus() == OrderPayStatus.NO) {
            Menu menu = menuRepository.findById(menuId);

            order.addMenu(menu);

            Money money = calculator.calculateMenus(order.getMember(), order.getMenuList());

            order.updatePrice(money);

            order.setUpdateAt(now);

        } else {
            throw new IllegalArgumentException("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다.");
        }

        return order;
    }

    @Transactional
    public Order updateRemoveMenu(Long memberId, int menuIdx, LocalDateTime now) {
        Order order = orderRepository.findByMemberId(memberId);

        if(order.getStatus() == OrderStatus.ORDER && order.getPayStatus() == OrderPayStatus.NO) {
            order.removeMenu(menuIdx);

            Money money = calculator.calculateMenus(order.getMember(), order.getMenuList());

            order.updatePrice(money);

            order.setUpdateAt(now);

        } else {
            throw new IllegalArgumentException("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다.");
        }

        return order;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAllByMemberId(Pageable pageable, Long memberId) {
        return orderRepository.findAllByMemberId(pageable, memberId);
    }

    public Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId, OrderStatus status) {
        return orderRepository.findAllByMemberIdAndOrderStatus(pageable, memberId, status);
    }

    public Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId, OrderPayStatus status) {
        return orderRepository.findAllByMemberIdAndPayStatus(pageable, memberId, status);
    }
}
