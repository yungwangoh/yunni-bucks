package sejong.coffee.yun.repository.review.menu.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.repository.review.menu.MenuReviewRepository;
import sejong.coffee.yun.repository.review.menu.jpa.JpaMenuReviewRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_REVIEW;
import static sejong.coffee.yun.domain.order.menu.QMenuReview.menuReview;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuReviewRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final JpaMenuReviewRepository jpaMenuReviewRepository;

    @Override
    @Transactional
    public MenuReview save(MenuReview menuReview) {
        return jpaMenuReviewRepository.save(menuReview);
    }

    @Override
    public MenuReview findById(Long reviewId) {
        return jpaMenuReviewRepository.findById(reviewId)
                .orElseThrow(NOT_FOUND_MENU_REVIEW::notFoundException);
    }

    @Override
    public List<MenuReview> findAll() {
        return jpaMenuReviewRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long reviewId) {
        jpaMenuReviewRepository.deleteById(reviewId);
    }

    @Override
    public Page<MenuReview> findAllByMemberId(Pageable pageable, Long memberId) {
        List<MenuReview> menuReviews = jpaQueryFactory.selectFrom(menuReview)
                .where(menuReview.member.id.eq(memberId))
                .orderBy(menuReview.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(menuReview.count())
                .from(menuReview);

        return PageableExecutionUtils.getPage(menuReviews, pageable, jpaQuery::fetchOne);
    }
}
