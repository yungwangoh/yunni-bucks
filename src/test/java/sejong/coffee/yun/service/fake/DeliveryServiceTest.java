package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.infra.DeliveryScheduler;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.FakeDeliveryRepository;
import sejong.coffee.yun.mock.repository.FakeOrderRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.cart.fake.FakeCartRepository;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.DeliveryService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static sejong.coffee.yun.domain.exception.ExceptionControl.DO_NOT_PAID;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_DELIVERY;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringJUnitConfig
@ContextConfiguration(classes = {
        DeliveryService.class,
        FakeUserRepository.class,
        FakeOrderRepository.class,
        FakeDeliveryRepository.class,
        FakeCartRepository.class,
        JwtProvider.class,
        DeliveryScheduler.class
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList",
        "schedules.cron.product=0/1 * * * * * "
})
public class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private FakeDeliveryRepository fakeDeliveryRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FakeOrderRepository fakeOrderRepository;
    @Autowired
    private FakeCartRepository fakeCartRepository;
    @Autowired
    private CartRepository cartRepository;

    Order order;
    Menu menu;
    List<CartItem> menuList = new ArrayList<>();
    Member saveMember;

    @BeforeEach
    void init() {
        Member member = Member.builder()
                .address(new Address("서울시", "광진구", "화양동", "123-432"))
                .userRank(UserRank.BRONZE)
                .name("홍길동")
                .password("qwer1234@A")
                .money(Money.ZERO)
                .email("qwer123@naver.com")
                .orderCount(0)
                .build();

        saveMember = userRepository.save(member);

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        CartItem cartItem = CartItem.builder()
                .menu(menu)
                .build();

        menuList.add(cartItem);

        Cart cart = cartRepository.save(Cart.builder().member(saveMember).cartItems(menuList).build());
        order = Order.createOrder(cart, Money.initialPrice(new BigDecimal("10000")), LocalDateTime.now());
    }

    @AfterEach
    void initDB() {
        fakeDeliveryRepository.clear();
        fakeUserRepository.clear();
        fakeOrderRepository.clear();
        fakeCartRepository.clear();
    }

    @Test
    void 예약_배달_저장() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        // when
        Delivery delivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeliveryType.RESERVE
        );

        // then
        assertThat(delivery.getOrder()).isEqualTo(saveOrder);
    }

    @Test
    void 일반_배달_저장() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        // when
        Delivery delivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // then
        assertThat(delivery.getOrder()).isEqualTo(saveOrder);
    }

    @Test
    void 배달_저장_할때_결제를_안_한_경우() {
        // given
        Order saveOrder = orderRepository.save(order);

        // when

        // then
        assertThatThrownBy(() -> deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), DeliveryType.NORMAL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(DO_NOT_PAID.getMessage());
    }

    @Test
    void 배달_취소() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        Delivery saveDelivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // when
        Delivery delivery = deliveryService.cancel(saveDelivery.getId());

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCEL);
    }

    @Test
    void 배달_취소는_준비상태에서만_취소할_수_있다() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        Delivery saveDelivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // when
        saveDelivery.delivery();

        // then
        assertThatThrownBy(() -> deliveryService.cancel(saveDelivery.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("취소가 불가능합니다.");
    }

    @Test
    void 배달_완료() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        Delivery saveDelivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        saveDelivery.delivery();

        // when
        Delivery delivery = deliveryService.complete(saveDelivery.getId());

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.COMPLETE);
    }

    @Test
    void 배달_완료는_배송상태에서만_완료가_가능하다() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        Delivery saveDelivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // when
        saveDelivery.cancel();

        // then
        assertThatThrownBy(() -> deliveryService.complete(saveDelivery.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("배송 완료가 불가능합니다.");
    }

    @Test
    void 예약_배달() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        LocalDateTime reserveTime = LocalDateTime.of(2023, 5, 30, 5, 30);
        deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), reserveTime, DeliveryType.RESERVE);

        // when
        deliveryService.reserveDelivery();

        // then
        assertThat(deliveryRepository.findAll())
                .isNotEmpty()
                .allSatisfy(delivery -> assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERY));
    }

    @Test
    void 예약_배달_하루마다_배달_상태를_갱신한다() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        // when
        LocalDateTime reserveTime = LocalDateTime.of(2023, 5, 30, 5, 30);
        deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), reserveTime, DeliveryType.RESERVE);

        // then
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    List<Delivery> deliveries = fakeDeliveryRepository.findAll();
                    assertThat(deliveries)
                            .isNotEmpty()
                            .allSatisfy(delivery -> assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERY));
                });
    }

    @Test
    void 일반_배달() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        Delivery saveDelivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // when
        Delivery delivery = deliveryService.normalDelivery(saveDelivery.getId());

        // then
        assertThat(delivery).isEqualTo(saveDelivery);
    }

    @Test
    void 배달_주소_수정() {
        // given
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        Delivery saveDelivery = deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // when
        Address address = new Address("경기도", "남양주시", "방화동 도산로 111", "110-100");
        LocalDateTime updateAt = LocalDateTime.of(2023, 5, 11, 11, 10);

        Delivery delivery = deliveryService.updateAddress(saveDelivery.getId(), address, updateAt);

        // then
        assertThat(delivery.getAddress()).isEqualTo(address);
        assertThat(delivery.getUpdateAt()).isEqualTo(updateAt);
    }

    @Test
    void 배달_내역을_찾을_수_없다() {
        // given
        Address address = new Address("경기도", "남양주시", "방화동 도산로 111", "110-100");

        // when

        // then
        assertThatThrownBy(() -> deliveryService.updateAddress(1L, address, LocalDateTime.now()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_DELIVERY.getMessage());
    }

    @Test
    void 유저의_배달_내역_조회() {
        // given
        int count = 10;
        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        IntStream.range(0, count).forEach(i -> deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        ));

        // when
        PageRequest pr = PageRequest.of(0, 10);

        Page<Delivery> page = deliveryService.findAllByMemberId(pr, saveMember.getId());

        // then
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RESERVE", "NORMAL"})
    void 유저의_배달_내역_조회_배달타입(DeliveryType type) {
        // given
        int typeCount = 10;

        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        IntStream.range(0, typeCount).forEach(i -> deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        ));

        IntStream.range(0, typeCount).forEach(i -> deliveryService.save(
                saveOrder.getId(),
                saveOrder.getMember().getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeliveryType.RESERVE
        ));

        // when
        PageRequest pr = PageRequest.of(0, 10);
        Page<Delivery> page = deliveryService.findDeliveryTypeAllByMemberId(pr, saveMember.getId(), type);

        // then
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(typeCount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"READY", "DELIVERY", "CANCEL", "COMPLETE"})
    void 유저의_배달_내역_조회_배달상태(DeliveryStatus status) {
        // given
        int statusCount = 10;

        Order saveOrder = orderRepository.save(order);

        saveOrder.completePayment();

        selectDeliveryStatus(status, statusCount, saveOrder);

        // when
        PageRequest pr = PageRequest.of(0, 10);
        Page<Delivery> page = deliveryService.findDeliveryStatusAllByMemberId(pr, saveMember.getId(), status);

        // then
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(statusCount);
    }

    private void selectDeliveryStatus(DeliveryStatus status, int statusCount, Order saveOrder) {
        switch (status) {
            case READY ->  IntStream.range(0, statusCount).forEach(i ->
                    deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), DeliveryType.NORMAL)
            );
            case CANCEL -> IntStream.range(0, statusCount).forEach(i ->
                {
                    Delivery save = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), DeliveryType.NORMAL);
                    save.cancel();
                }
            );
            case COMPLETE -> IntStream.range(0, statusCount).forEach(i ->
                        {
                            Delivery save = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), DeliveryType.NORMAL);
                            save.delivery();
                            save.complete();
                        }
                );
            case DELIVERY -> IntStream.range(0, statusCount).forEach(i ->
                    {
                        Delivery save = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(), LocalDateTime.now(), DeliveryType.NORMAL);
                        save.delivery();
                    }
            );
        }
    }
}
