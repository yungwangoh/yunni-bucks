package sejong.coffee.yun.repository.order.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.order.jpa.JpaOrderRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_ORDER;
import static sejong.coffee.yun.domain.order.QOrder.order;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
       return jpaOrderRepository.findById(id)
               .orElseThrow(NOT_FOUND_ORDER::notFoundException);
    }

    @Override
    public Order findByMemberId(Long memberId) {
        return jpaOrderRepository.findByCartMemberId(memberId)
                .orElseThrow(NOT_FOUND_ORDER::notFoundException);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public Page<Order> findAllByMemberId(Pageable pageable, Long memberId) {
        List<Order> orders = jpaQueryFactory.selectFrom(order)
                .where(order.cart.member.id.eq(memberId))
                .join(order.cart).fetchJoin()
                .orderBy(order.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(order.count())
                .from(order);

        return PageableExecutionUtils.getPage(orders, pageable, jpaQuery::fetchOne);
    }

    @Override
    public Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId, OrderStatus status) {
        List<Order> orders = jpaQueryFactory.selectFrom(order)
                .where(order.cart.member.id.eq(memberId))
                .where(order.status.eq(status))
                .join(order.cart).fetchJoin()
                .orderBy(order.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(order.count())
                .from(order);

        return PageableExecutionUtils.getPage(orders, pageable, jpaQuery::fetchOne);
    }

    @Override
    public Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId, OrderPayStatus status) {
        List<Order> orders = jpaQueryFactory.selectFrom(order)
                .where(order.cart.member.id.eq(memberId))
                .where(order.payStatus.eq(status))
                .join(order.cart).fetchJoin()
                .orderBy(order.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(order.count())
                .from(order);

        return PageableExecutionUtils.getPage(orders, pageable, jpaQuery::fetchOne);
    }

    @Override
    public void clear() {
        jpaOrderRepository.deleteAll();
    }
}
