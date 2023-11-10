package sejong.coffee.yun.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.service.OrderService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderServiceFacade {

    private final OrderService orderService;

    public void order(Long memberId, LocalDateTime now) throws InterruptedException {

        while (true) {
            try {
                orderService.order(memberId, now);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                Thread.sleep(50);
            }
        }
    }
}
