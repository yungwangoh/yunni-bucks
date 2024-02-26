package sejong.coffee.yun.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.facade.OrderServiceFacade;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.coupon.CouponRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.command.CartServiceCommand;
import sejong.coffee.yun.service.command.CouponServiceCommand;
import sejong.coffee.yun.service.command.OrderServiceCommand;
import sejong.coffee.yun.service.command.UserServiceCommand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserConcurrencyTest extends MainIntegrationTest {

    @Autowired
    private UserServiceCommand userService;
    @Autowired
    private OrderServiceFacade orderServiceFacade;
    @Autowired
    private CouponServiceCommand couponService;
    @Autowired
    private OrderServiceCommand orderService;
    @Autowired
    private CartServiceCommand cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CartRepository cartRepository;

    private Member member;
    private Menu menu;
    private Coupon coupon;

    @BeforeEach
    void init() {
        member = userRepository.save(member());
        menu = menuRepository.save(bread());
        coupon = couponRepository.save(coupon(100));
    }

    @Test
    void 유저의_주문_개수_동시성_처리_낙관적_락() throws Exception {
        // given
        int orderCount = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(orderCount);

        CountDownLatch latch = new CountDownLatch(orderCount);

        cartService.createCart(member.getId());
        cartService.addMenu(member.getId(), menu.getId());

        // when
        for(int i = 0; i < orderCount; i++) {
            executorService.submit(() -> {
                orderServiceFacade.order(member.getId(), LocalDateTime.now());
                latch.countDown();
            });
        }
        latch.await();

        Member findMember = userRepository.findById(member.getId());

        // then
        assertThat(findMember.getOrderCount()).isEqualTo(orderCount);
        assertEquals(3, findMember.getVersion());
    }

    @Test
    void 유저들이_쿠폰을_등록했을_때_쿠폰_재고_감소에_대한_동시성_처리_비관적_락() throws Exception {
        // given
        int memberCount = 5;

        List<Member> members = Stream.generate(() -> userRepository.save(member()))
                .limit(memberCount)
                .toList();

        ExecutorService executorService = Executors.newFixedThreadPool(memberCount);
        CountDownLatch latch = new CountDownLatch(memberCount);

        // when
        members.forEach(member -> executorService.submit(() -> {
                couponService.couponRegistry(coupon.getId(), member.getId(), coupon.getCreateAt());
                latch.countDown();
        }));
        latch.await();

        Coupon c = couponRepository.findById(coupon.getId());

        // then
        assertEquals(c.getQuantity(), 100 - memberCount);
    }

    @Test
    void 유저들이_주문을_할_때_메뉴_재고_감소에_대한_동시성_처리_낙관적_락() throws Exception {
        // given
        int memberCount = 10;

        List<Member> members = Stream.generate(() -> userRepository.save(member()))
                .limit(memberCount)
                .toList();

        members.forEach(member -> {
            cartService.createCart(member.getId());
            cartService.addMenu(member.getId(), menu.getId());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(memberCount);

        // when
        members.forEach(member -> executorService.submit(() -> {
            orderServiceFacade.order(member.getId(), LocalDateTime.now());
            latch.countDown();
        }));
        latch.await();

        Menu m = menuRepository.findById(menu.getId());

        // then
        assertEquals(m.getQuantity(), 10000 - memberCount);
    }

    @Test
    void 메뉴의_주문_개수_동시성_처리_낙관적_락() throws Exception {
        // given
        int memberCount = 10;

        List<Member> members = Stream.generate(() -> userRepository.save(member()))
                .limit(memberCount)
                .toList();

        members.forEach(member -> {
            cartService.createCart(member.getId());
            cartService.addMenu(member.getId(), menu.getId());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(memberCount);

        // when
        members.forEach(member -> executorService.submit(() -> {
            orderServiceFacade.order(member.getId(), LocalDateTime.now());
            latch.countDown();
        }));
        latch.await();

        Menu findMenu = menuRepository.findById(menu.getId());

        // then
        assertEquals(findMenu.getOrderCount(), memberCount);
    }
}
