package sejong.coffee.yun.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static sejong.coffee.yun.domain.exception.ExceptionControl.DO_NOT_PAID;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_DELIVERY;

@ExtendWith(MockitoExtension.class)
@Disabled("mock test disabled")
class DeliveryServiceTest {

    @InjectMocks
    DeliveryService deliveryService;
    @Mock
    DeliveryRepository deliveryRepository;
    @Mock
    OrderRepository orderRepository;

    static Order order;
    static Menu menu;
    static List<Menu> menuList = new ArrayList<>();
    static Member member;
    static ReserveDelivery reserveDelivery;
    static NormalDelivery normalDelivery;
    static Page<Delivery> deliveryPage;

    @BeforeAll
    static void init() {
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
        menu = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());

        menuList.add(menu);
        order = Order.createOrder(member, menuList, Money.initialPrice(new BigDecimal("10000")), LocalDateTime.now());

        reserveDelivery = ReserveDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY,
                LocalDateTime.now()
        );

        normalDelivery = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY
        );

        PageRequest pr = PageRequest.of(0, 10);
        List<Delivery> deliveries = List.of(reserveDelivery, normalDelivery);
        deliveryPage = new PageImpl<>(deliveries, pr, deliveries.size());
    }

    @Test
    void 예약_배달_저장() {
        // given
        given(orderRepository.findById(anyLong())).willReturn(order);
        given(deliveryRepository.save(any())).willReturn(reserveDelivery);

        order.completePayment();

        // when
        Delivery delivery = deliveryService.save(
                1L,
                member.getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                DeliveryType.RESERVE
        );

        // then
        assertThat(delivery).isEqualTo(reserveDelivery);
    }

    @Test
    void 일반_배달_저장() {
        // given
        given(orderRepository.findById(anyLong())).willReturn(order);
        given(deliveryRepository.save(any())).willReturn(normalDelivery);

        order.completePayment();

        // when
        Delivery delivery = deliveryService.save(
                1L,
                member.getAddress(),
                LocalDateTime.now(),
                DeliveryType.NORMAL
        );

        // then
        assertThat(delivery).isEqualTo(normalDelivery);
    }

    @Test
    void 배달_저장_할때_결제를_안_한_경우() {
        // given
        given(orderRepository.findById(anyLong())).willThrow(new IllegalArgumentException(DO_NOT_PAID.getMessage()));

        // when

        // then
        assertThatThrownBy(() -> deliveryService.save(1L, member.getAddress(), LocalDateTime.now(), DeliveryType.NORMAL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(DO_NOT_PAID.getMessage());
    }

    @Test
    void 배달_취소() {
        // given
        NormalDelivery d = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY
        );

        given(deliveryRepository.findOne(anyLong())).willReturn(d);

        // when
        Delivery delivery = deliveryService.cancel(1L);

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCEL);
    }

    @Test
    void 배달_취소는_준비상태에서만_취소할_수_있다() {
        // given
        NormalDelivery d = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.DELIVERY
        );
        given(deliveryRepository.findOne(anyLong())).willReturn(d);

        // when

        // then
        assertThatThrownBy(() -> deliveryService.cancel(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("취소가 불가능합니다.");
    }

    @Test
    void 배달_완료() {
        // given
        NormalDelivery d = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.DELIVERY
        );
        given(deliveryRepository.findOne(anyLong())).willReturn(d);

        // when
        Delivery delivery = deliveryService.complete(1L);

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.COMPLETE);
    }

    @Test
    void 배달_완료는_배송상태에서만_완료가_가능하다() {
        // given
        NormalDelivery d = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY
        );
        given(deliveryRepository.findOne(anyLong())).willReturn(d);

        // when

        // then
        assertThatThrownBy(() -> deliveryService.complete(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("배송 완료가 불가능합니다.");
    }

    @Test
    void 예약_배달() {
        // given
        List<Delivery> deliveries = new ArrayList<>();
        given(deliveryRepository.findAll()).willReturn(deliveries);

        // when
        deliveryService.reserveDelivery();

        // then
        for(var a : deliveries) {
            then(a).should(times(1)).delivery();
        }
    }

    @Test
    void 일반_배달() {
        // given
        given(deliveryRepository.findOne(anyLong())).willReturn(normalDelivery);

        // when
        Delivery delivery = deliveryService.normalDelivery(1L);

        // then
        assertThat(delivery).isEqualTo(normalDelivery);
    }

    @Test
    void 배달_주소_수정() {
        // given
        given(deliveryRepository.findOne(anyLong())).willReturn(reserveDelivery);

        // when
        LocalDateTime updateAt = LocalDateTime.of(2023, 5, 11, 11, 10);

        Delivery delivery = deliveryService.updateAddress(1L, member.getAddress(), updateAt);

        // then
        assertThat(delivery.getAddress()).isEqualTo(member.getAddress());
        assertThat(delivery.getUpdateAt()).isEqualTo(updateAt);
    }

    @Test
    void 배달_내역을_찾을_수_없다() {
        // given
        given(deliveryRepository.findOne(anyLong())).willThrow(NOT_FOUND_DELIVERY.notFoundException());

        // when

        // then
        assertThatThrownBy(() -> deliveryService.updateAddress(1L, member.getAddress(), LocalDateTime.now()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_DELIVERY.getMessage());
    }

    @Test
    void 유저의_배달_내역_조회() {
        // given
        given(deliveryRepository.findByMemberId(any(), anyLong())).willReturn(deliveryPage);

        // when
        PageRequest pr = PageRequest.of(0, 10);
        Page<Delivery> page = deliveryService.findAllByMemberId(pr, 1L);

        // then
        assertThat(page).isEqualTo(deliveryPage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RESERVE", "NORMAL"})
    void 유저의_배달_내역_조회_배달타입(DeliveryType type) {
        // given
        given(deliveryRepository.findDeliveryTypeByMemberId(any(), anyLong(), any())).willReturn(deliveryPage);

        // when
        PageRequest pr = PageRequest.of(0, 10);
        Page<Delivery> page = deliveryService.findDeliveryTypeAllByMemberId(pr, 1L, type);

        // then
        assertThat(page).isEqualTo(deliveryPage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"READY", "DELIVERY", "CANCEL", "COMPLETE"})
    void 유저의_배달_내역_조회_배달상태(DeliveryStatus status) {
        // given
        given(deliveryRepository.findDeliveryStatusByMemberId(any(), anyLong(), any())).willReturn(deliveryPage);

        // when
        PageRequest pr = PageRequest.of(0, 10);
        Page<Delivery> page = deliveryService.findDeliveryStatusAllByMemberId(pr, 1L, status);

        // then
        assertThat(page).isEqualTo(deliveryPage);
    }
}