package sejong.coffee.yun.service.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.OrderService;
import sejong.coffee.yun.service.UserService;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class UserConcurrencyTest extends MainIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuRepository menuRepository;

    private Member member;
    private Menu menu;

    @BeforeEach
    void init() {
        member = userRepository.save(member());
        menu = menuRepository.save(bread());

    }

    @Test
    void 유저의_주문_개수_동시성_처리_낙관적_락() throws Exception {
        // given
        int orderCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        CountDownLatch latch = new CountDownLatch(orderCount);

        cartService.createCart(member.getId());
        cartService.addMenu(member.getId(), menu.getId());


        // when
        for(int i = 0; i < orderCount; i++) {
            executorService.submit(() -> {
                try {
                    orderService.order(member.getId(), LocalDateTime.now());
                } catch (OptimisticLockException e) {
                    System.out.println("error = " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            Thread.sleep(10);
        }

        latch.await();

        Member findMember = userRepository.findById(member.getId());

        // then
        assertThat(findMember.getOrderCount()).isEqualTo(orderCount);
    }
}
