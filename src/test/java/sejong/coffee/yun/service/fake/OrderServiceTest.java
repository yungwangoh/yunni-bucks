package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.discount.condition.RankCondition;
import sejong.coffee.yun.domain.discount.policy.PercentPolicy;
import sejong.coffee.yun.domain.order.Calculator;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.FakeMenuRepository;
import sejong.coffee.yun.mock.repository.FakeOrderRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        OrderService.class,
        FakeUserRepository.class,
        FakeOrderRepository.class,
        JwtProvider.class,
        Calculator.class,
        PercentPolicy.class,
        RankCondition.class,
        FakeMenuRepository.class
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList"
})
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private FakeOrderRepository fakeOrderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FakeMenuRepository fakeMenuRepository;

    Member member;
    List<Menu> menuList = new ArrayList<>();

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

        Menu menu1 = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        menuList.add(menu1);
    }

    @AfterEach
    void initDB() {
        fakeOrderRepository.clear();
        fakeUserRepository.clear();
        fakeMenuRepository.clear();
    }

    @Test
    void 주문() {
        // given
        Member save = userRepository.save(member);

        // when
        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        // then
        assertThat(order.getMember()).isEqualTo(save);
        assertThat(order.getMenuList()).isEqualTo(menuList);
    }

    @Test
    void 유저가_주문한_시간() {
        // given
        Member save = userRepository.save(member);
        LocalDateTime orderTime = LocalDateTime.of(2022, 11, 20, 11, 20);

        // when
        Order order = orderService.order(save.getId(), menuList, orderTime);

        // then
        assertThat(order.getCreateAt()).isEqualTo(orderTime);
    }

    @Test
    void 주문을_조회한다() {
        // given
        Member save = userRepository.save(member);
        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        // when
        Order findOrder = orderService.findOrder(order.getId());

        // then
        assertThat(findOrder).isEqualTo(order);
    }

    @Test
    void 주문_리스트_조회() {
        // given
        int size = 10;
        Member save = userRepository.save(member);

        IntStream.range(0, size).forEach(i -> orderService.order(save.getId(), menuList, LocalDateTime.now()));

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders.size()).isEqualTo(size);
    }

    @Test
    void 주문_수정_시간() {
        // given
        Member save = userRepository.save(member);

        LocalDateTime initTime = LocalDateTime.now();
        Order order = orderService.order(save.getId(), menuList, initTime);

        LocalDateTime updateTime = LocalDateTime.of(2022, 11, 20, 11, 20);

        // when
        order.setUpdateAt(updateTime);

        // then
        assertThat(order.getUpdateAt()).isEqualTo(updateTime);
    }

    @Test
    void 주문_총_금액_확인() {
        // given
        Member save = userRepository.save(member);

        // when
        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        // then
        assertThat(order.fetchTotalOrderPrice()).isEqualTo(menuList.get(0).getPrice().getTotalPrice());
    }

    @Test
    void 주문명_확인() {
        // given
        Member save = userRepository.save(member);

        // when
        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        // then
        assertThat(order.getName()).isEqualTo(menuList.get(0).getTitle() + " 외 " + menuList.size() + "개");
    }

    @Test
    void 유저가_주문_하고_주문_개수_확인() {
        // given
        Member save = userRepository.save(member);

        // when
        orderService.order(save.getId(), menuList, LocalDateTime.now());

        // then
        assertThat(save.getOrderCount()).isEqualTo(1);
    }

    @Test
    void 유저가_주문취소된_상태에서_메뉴_수정할_때_예외() {
        // given
        Member save = userRepository.save(member);

        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        Menu menu = menuRepository.save(menuList.get(0));

        // when
        order.cancel();

        // then
        assertThatThrownBy(() -> orderService.updateAddMenu(save.getId(), menu.getId(), LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다.");
    }

    @Test
    void 유저가_결제가된_상태에서_메뉴_수정할_때_예외() {
        // given
        Member save = userRepository.save(member);

        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        Menu menu = menuRepository.save(menuList.get(0));

        // when
        order.completePayment();

        // then
        assertThatThrownBy(() -> orderService.updateAddMenu(save.getId(), menu.getId(), LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다.");
    }

    @Test
    void 유저가_주문을_변경한다_메뉴추가() {
        // given
        Member save = userRepository.save(member);

        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        Menu menu = menuRepository.save(menuList.get(0));

        // when
        Order addMenu = orderService.updateAddMenu(save.getId(), menu.getId(), LocalDateTime.now());

        // then
        assertThat(order.getMenuList().size()).isEqualTo(2);
    }

    @Test
    void 유저가_주문을_변경한다_메뉴삭제() {
        // given
        Member save = userRepository.save(member);

        Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

        Menu menu = menuRepository.save(menuList.get(0));

        // when
        Order removeMenu = orderService.updateRemoveMenu(save.getId(), 0, LocalDateTime.now());

        // then
        assertThat(removeMenu.getMenuList().size()).isEqualTo(0);
    }

    @Test
    void 유저가_주문한_내역() {
        // given
        Member save = userRepository.save(member);

        orderService.order(save.getId(), menuList, LocalDateTime.now());

        PageRequest pr = PageRequest.of(0,10);

        // when
        Page<Order> orderPage = orderService.findAllByMemberId(pr, save.getId());

        // then
        assertThat(orderPage.getContent()).isEqualTo(orderService.findAll());
        assertThat(orderPage.getTotalPages()).isEqualTo(1);
        assertThat(orderPage.getTotalElements()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ORDER", "CANCEL"})
    void 유저가_주문한_내역_주문상태(OrderStatus status) {
        // given
        int statusCount = 10;
        Member save = userRepository.save(member);

        IntStream.range(0, statusCount).forEach(i -> {
            Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

            order.cancel();
        });

        IntStream.range(0, statusCount).forEach(i -> {
            orderService.order(save.getId(), menuList, LocalDateTime.now());
        });

        PageRequest pr = PageRequest.of(0, 10);

        // when
        Page<Order> orderPage = orderService.findAllByMemberIdAndOrderStatus(pr, save.getId(), status);

        // then
        assertThat(orderPage.getTotalElements()).isEqualTo(statusCount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"YES", "NO"})
    void 유저가_주문하고_결제한_내역(OrderPayStatus status) {
        // given
        int statusCount = 10;
        Member save = userRepository.save(member);

        IntStream.range(0, statusCount).forEach(i -> {
            Order order = orderService.order(save.getId(), menuList, LocalDateTime.now());

            order.completePayment();
        });

        IntStream.range(0, statusCount).forEach(i -> {
            orderService.order(save.getId(), menuList, LocalDateTime.now());
        });

        PageRequest pr = PageRequest.of(0, 10);

        // when
        Page<Order> orderPage = orderService.findAllByMemberIdAndPayStatus(pr, save.getId(), status);

        // then
        assertThat(orderPage.getTotalElements()).isEqualTo(statusCount);
    }
}
