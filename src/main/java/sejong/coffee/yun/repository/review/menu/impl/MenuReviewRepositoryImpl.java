package sejong.coffee.yun.repository.review.menu.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.repository.review.menu.MenuReviewRepository;
import sejong.coffee.yun.repository.review.menu.jpa.JpaMenuReviewRepository;
import sejong.coffee.yun.util.querydsl.MenuReviewQueryDslUtil;

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.StringTokenizer;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_REVIEW;
import static sejong.coffee.yun.domain.order.menu.QMenuReview.menuReview;
import static sejong.coffee.yun.domain.user.QMember.member;

@Repository
@Primary
@RequiredArgsConstructor
public class MenuReviewRepositoryImpl implements MenuReviewRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final JpaMenuReviewRepository jpaMenuReviewRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

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
    public MenuReview findByMemberIdAndId(Long memberId, Long reviewId) {
        return jpaMenuReviewRepository.findByMemberIdAndId(memberId, reviewId)
                .orElseThrow(NOT_FOUND_MENU_REVIEW::notFoundException);
    }

    @Override
    public List<MenuReview> findAll() {
        return jpaMenuReviewRepository.findAll();
    }

    @Override
    public List<MenuReview> findByCommentsContaining(String searchComment) {
        return this.searchFullTextQueryDsl(searchComment);
    }

    @Override
    public List<MenuReview> fullTextSearchComments(String searchComment) {
        return this.searchFullTextJdbc(searchComment);
    }

    @Override
    public List<MenuReview> fullTextSearchCommentsNative(String searchComment) {
        return jpaMenuReviewRepository.searchFullTextComments(searchComment);
    }

    @Override
    @Transactional
    public void delete(Long reviewId) {
        try {
            jpaMenuReviewRepository.deleteById(reviewId);
        } catch (Exception e) {
            throw NOT_FOUND_MENU_REVIEW.notFoundException();
        }
    }

    @Override
    @Transactional
    public void delete(Long memberId, Long reviewId) {
        try {
            jpaMenuReviewRepository.deleteByMemberIdAndId(memberId, reviewId);
        } catch (Exception e) {
            throw NOT_FOUND_MENU_REVIEW.notFoundException();
        }
    }

    @Override
    public Page<MenuReview> findAllByMemberId(Pageable pageable, Long memberId) {
        List<MenuReview> menuReviews = jpaQueryFactory.selectFrom(menuReview)
                .where(menuReview.member.id.eq(memberId))
                .join(menuReview.member, member).fetchJoin()
                .orderBy(menuReview.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(menuReview.count())
                .from(menuReview);

        return PageableExecutionUtils.getPage(menuReviews, pageable, jpaQuery::fetchOne);
    }

    @Override
    public Page<MenuReview> findAllByMenuId(Pageable pageable, Long menuId) {
        List<MenuReview> menuReviews = jpaQueryFactory.selectFrom(menuReview)
                .where(menuReview.menu.id.eq(menuId))
                .join(menuReview.member, member).fetchJoin()
                .orderBy(menuReview.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaQuery = jpaQueryFactory.select(menuReview.count())
                .from(menuReview);

        return PageableExecutionUtils.getPage(menuReviews, pageable, jpaQuery::fetchOne);
    }

    @Override
    public void clear() {
        jpaMenuReviewRepository.deleteAll();
    }

    @Override
    @Transactional
    public void bulkInsert(int size, List<MenuReview> reviews) {

        String sql = "insert into menu_review (id, comments, create_at, update_at, member_id, menu_id) " +
                "values (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, reviews.get(i).getId());
                ps.setString(2, reviews.get(i).getComments());
                ps.setTimestamp(3, Timestamp.valueOf(reviews.get(i).getCreateAt()));
                ps.setTimestamp(4, Timestamp.valueOf(reviews.get(i).getUpdateAt()));
                ps.setLong(5, reviews.get(i).getMember().getId());
                ps.setLong(6, reviews.get(i).getMenu().getId());
            }

            @Override
            public int getBatchSize() {
                return size;
            }
        });
    }

    @Override
    @Transactional
    public void bulkDelete() {
        jpaQueryFactory.delete(menuReview)
                .execute();

        em.clear();
        em.flush();
    }

    private List<MenuReview> searchFullTextJdbc(String keyword) {

        String sql = "SELECT * FROM menu_review mr WHERE MATCH (mr.comments) AGAINST (? IN BOOLEAN MODE )";

        return jdbcTemplate.query(sql, this.mapMenuReview(), keyword);
    }

    private List<MenuReview> searchFullTextLikeJdbc(String keyword) {

        String sql = "SELECT * FROM menu_review mr " +
                "WHERE mr.comments LIKE CONCAT('%', ?, '%') ";

        return jdbcTemplate.query(sql, this.mapMenuReview(), keyword);
    }

    private RowMapper<MenuReview> mapMenuReview() {
        return ((rs, rowNum) -> {
            MenuReview menuReview = MenuReview.builder()
                    .id(rs.getLong("id"))
                    .comments(rs.getString("comments"))
                    .now(rs.getTimestamp("create_at").toLocalDateTime())
                    .build();
            menuReview.setUpdateAt(rs.getTimestamp("update_at").toLocalDateTime());

            return menuReview;
        });
    }

    private List<MenuReview> searchFullTextQueryDsl(String searchComment) {

        StringTokenizer st = new StringTokenizer(searchComment);

        return jpaQueryFactory.selectFrom(menuReview)
                .where(MenuReviewQueryDslUtil.containCheckStrings(st))
                .fetch();
    }
}
