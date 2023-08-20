package sejong.coffee.yun.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.service.DeliveryService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryScheduler {

    private final DeliveryService deliveryService;

    @Scheduled(cron = "${schedules.cron.product}")
    public void processReserveScheduler() {
        deliveryService.reserveDelivery();
    }
}
