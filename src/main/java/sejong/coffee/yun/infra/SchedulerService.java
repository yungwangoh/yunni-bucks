package sejong.coffee.yun.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.service.command.DeliveryServiceCommand;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class SchedulerService {

    private final DeliveryServiceCommand deliveryService;

    @Scheduled(cron = "${schedules.cron.product}")
    public void processReserveScheduler() {
        log.info("schedule START, current thread = {} ", Thread.currentThread().getName());
        LocalDateTime now = LocalDateTime.now().withMinute(0);
        deliveryService.reserveDelivery(now);
        log.info("schedule END, current thread = {} ", Thread.currentThread().getName());
    }

}
