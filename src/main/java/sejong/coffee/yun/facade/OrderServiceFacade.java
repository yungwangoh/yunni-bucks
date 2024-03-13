package sejong.coffee.yun.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.service.command.OrderServiceCommand;
import sejong.coffee.yun.service.query.OrderServiceQuery;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderServiceFacade {

    private final OrderServiceCommand orderService;
    private final OrderServiceQuery orderServiceQuery;

    public void order(Long memberId, LocalDateTime now) {

        while (true) {
            try {
                orderService.order(memberId, now);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                sleep(50);
            }
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}
