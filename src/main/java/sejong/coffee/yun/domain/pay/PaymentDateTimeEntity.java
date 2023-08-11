package sejong.coffee.yun.domain.pay;


import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class PaymentDateTimeEntity {

    /**
     * <결제 발생, 결제 승인>
     * yyyy-MM-dd'T'HH:mm:ss±hh:mm ISO 8601 형식
     */
    @Column(updatable = false)
    private LocalDateTime requestedAt; // 결제가 일어난 날짜와 시간 정보, 자동 생성 및 저장

    @Column(updatable = false)
    private LocalDateTime approvedAt; // 결제 승인이 일어난 날짜와 시간 정보
}
