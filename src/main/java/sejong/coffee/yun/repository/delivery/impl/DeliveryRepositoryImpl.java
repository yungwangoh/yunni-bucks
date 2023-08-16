package sejong.coffee.yun.repository.delivery.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.delivery.jpa.JpaDeliveryRepository;

import java.util.List;

import static sejong.coffee.yun.domain.delivery.QDelivery.delivery;


@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final JpaDeliveryRepository jpaDeliveryRepository;

    @Override
    @Transactional
    public Delivery save(Delivery delivery) {
        return jpaDeliveryRepository.save(delivery);
    }

    @Override
    public Page<Delivery> findByMemberId(Pageable pageable, Long memberId) {
        List<Delivery> deliveries = jpaQueryFactory.selectFrom(delivery)
                .where(delivery.order.member.id.eq(memberId)).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(delivery.createAt.desc())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(delivery.count())
                .from(delivery);

        return PageableExecutionUtils.getPage(deliveries, pageable, jpaQuery::fetchOne);
    }

    @Override
    public Page<Delivery> findDeliveryStatusByMemberId(Pageable pageable, Long memberId) {
        List<Delivery> deliveries = jpaQueryFactory.selectFrom(delivery)
                .where(delivery.order.member.id.eq(memberId)).fetchJoin()
                .where(delivery.status.eq(DeliveryStatus.RESERVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(delivery.createAt.desc())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(delivery.count())
                .from(delivery);

        return PageableExecutionUtils.getPage(deliveries, pageable, jpaQuery::fetchOne);
    }
}
