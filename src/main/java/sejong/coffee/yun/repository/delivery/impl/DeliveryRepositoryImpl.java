package sejong.coffee.yun.repository.delivery.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.delivery.jpa.JpaDeliveryRepository;

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static sejong.coffee.yun.domain.delivery.QDelivery.delivery;
import static sejong.coffee.yun.domain.delivery.QReserveDelivery.reserveDelivery;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_DELIVERY;
import static sejong.coffee.yun.domain.order.QOrder.order;
import static sejong.coffee.yun.domain.user.QCart.cart;


@Repository
@Primary
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final JpaDeliveryRepository jpaDeliveryRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    @Override
    @Transactional
    public Delivery save(Delivery delivery) {
        return jpaDeliveryRepository.save(delivery);
    }

    @Override
    public Delivery findOne(Long deliveryId) {
        return jpaDeliveryRepository.findById(deliveryId)
                .orElseThrow(NOT_FOUND_DELIVERY::notFoundException);
    }

    @Override
    public List<Delivery> findAll() {
        return jpaDeliveryRepository.findAll();
    }

    @Override
    public List<Delivery> findAllByReserveType() {
        return null;
    }

    @Override
    public Page<Delivery> findByMemberId(Pageable pageable, Long memberId) {
        List<Delivery> deliveries = jpaQueryFactory.selectFrom(delivery)
                .join(delivery.order, order).fetchJoin()
                .join(order.cart, cart).fetchJoin()
                .where(cart.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(delivery.createAt.desc())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(delivery.count())
                .from(delivery);

        return PageableExecutionUtils.getPage(deliveries, pageable, jpaQuery::fetchOne);
    }

    @Override
    public Page<Delivery> findDeliveryTypeByMemberId(Pageable pageable, Long memberId, DeliveryType type) {
        List<Delivery> deliveries = jpaQueryFactory.selectFrom(delivery)
                .join(delivery.order, order).fetchJoin()
                .join(order.cart, cart).fetchJoin()
                .where(cart.member.id.eq(memberId))
                .where(delivery.type.eq(type))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(delivery.createAt.desc())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(delivery.count())
                .from(delivery);

        return PageableExecutionUtils.getPage(deliveries, pageable, jpaQuery::fetchOne);
    }

    @Override
    public Page<Delivery> findDeliveryStatusByMemberId(Pageable pageable, Long memberId, DeliveryStatus status) {
        List<Delivery> deliveries = jpaQueryFactory.selectFrom(delivery)
                .join(delivery.order, order).fetchJoin()
                .join(order.cart, cart).fetchJoin()
                .where(cart.member.id.eq(memberId))
                .where(delivery.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(delivery.createAt.desc())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(delivery.count())
                .from(delivery);

        return PageableExecutionUtils.getPage(deliveries, pageable, jpaQuery::fetchOne);
    }

    @Override
    @Transactional
    public void clear() {
        jpaDeliveryRepository.deleteAll();
    }

    @Override
    @Transactional
    public void bulkInsert(int size, List<Delivery> deliveries, String dType, LocalDateTime reserveAt) {

        String sql = "insert into delivery (dtype, id, city, detail, district, zip_code, create_at, status, type, update_at, reserve_at, order_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, dType);
                ps.setLong(2, deliveries.get(i).getId());
                ps.setString(3, deliveries.get(i).getAddress().getCity());
                ps.setString(4, deliveries.get(i).getAddress().getDetail());
                ps.setString(5, deliveries.get(i).getAddress().getDistrict());
                ps.setString(6, deliveries.get(i).getAddress().getZipCode());
                ps.setTimestamp(7, Timestamp.valueOf(deliveries.get(i).getCreateAt()));
                ps.setString(8, deliveries.get(i).getStatus().toString());
                ps.setString(9, deliveries.get(i).getType().toString());
                ps.setTimestamp(10, Timestamp.valueOf(deliveries.get(i).getUpdateAt()));
                ps.setTimestamp(11, Timestamp.valueOf(reserveAt));
                ps.setLong(12, deliveries.get(i).getOrder().getId());
            }

            @Override
            public int getBatchSize() {
                return deliveries.size();
            }
        });
    }

    @Override
    @Transactional
    public Long bulkUpdate(LocalDateTime reserveAt) {

        long execute = jpaQueryFactory.update(reserveDelivery)
                .where(reserveDelivery.type.eq(DeliveryType.RESERVE).and(reserveDelivery.reserveAt.eq(reserveAt)))
                .set(reserveDelivery.status, DeliveryStatus.DELIVERY)
                .execute();

        em.clear();
        em.flush();

        if(execute <= 0) throw new RuntimeException("fail bulk update!!");

        return execute;
    }

    @Override
    @Transactional
    public void bulkDelete() {
        jpaQueryFactory.delete(delivery)
                .execute();

        em.flush();
        em.clear();
    }
}
