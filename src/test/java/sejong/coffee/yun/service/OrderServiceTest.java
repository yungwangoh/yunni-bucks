package sejong.coffee.yun.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private Calculator calculator;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    private Member member;
    private Order order;
    private List<Menu> menuList = new ArrayList<>();

    @BeforeEach
    void init() {
        member = Member.builder()
                .address(new Address("서울시", "광진구", "화양동", "123-432"))
                .userRank(UserRank.BRONZE)
                .name("홍길동")
                .password("qwer1234@A")
                .money(Money.ZERO)
                .email("qwer123@naver.com")
                .orderCount(0)
                .build();

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        Menu menu1 = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);

        menuList.add(menu1);
        order = Order.createOrder(member, menuList, Money.initialPrice(new BigDecimal("10000")));
    }

    @Test
    void 주문() {
        // given
        given(orderRepository.save(any())).willReturn(order);
        given(userRepository.findById(any())).willReturn(member);

        // when
        Order saveOrder = orderService.order(1L, menuList);

        // then
        assertThat(saveOrder).isEqualTo(order);
    }

    @Test
    void 주문을_조회한다() {
        // given
        given(orderRepository.findById(any())).willReturn(order);

        // when
        Order findOrder = orderService.findOrder(order.getId());

        // then
        assertThat(findOrder).isEqualTo(order);
    }

    @Test
    void 주문_리스트_조회() {
        // given
        given(orderRepository.findAll()).willReturn(List.of(order));

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).isEqualTo(List.of(order));
    }

    @Test
    void 주문_총_금액_확인() {
        // given
        given(orderRepository.save(any())).willReturn(order);
        given(userRepository.findById(any())).willReturn(member);
        given(calculator.calculateMenus(any(), any())).willReturn(Money.initialPrice(new BigDecimal("10000")));

        // when
        Order saveOrder = orderService.order(1L, menuList);

        // then
        assertThat(saveOrder.fetchTotalOrderPrice()).isEqualTo(new BigDecimal("10000"));
    }

    @Test
    void 주문명_확인() {
        // given
        given(orderRepository.save(any())).willReturn(order);
        given(userRepository.findById(any())).willReturn(member);

        // when
        Order saveOrder = orderService.order(1L, menuList);

        // then
        assertThat(saveOrder.getName()).isEqualTo("커피 외 1개");
    }

    @Test
    void 유저가_주문_하고_주문_개수_확인() {
        // given
        given(orderRepository.save(any())).willReturn(order);
        given(userRepository.findById(any())).willReturn(member);

        // when
        Order saveOrder = orderService.order(1L, menuList);

        // then
        assertThat(saveOrder.getMember().getOrderCount()).isEqualTo(1);
    }
}