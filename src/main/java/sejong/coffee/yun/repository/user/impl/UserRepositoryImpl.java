package sejong.coffee.yun.repository.user.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.repository.user.jpa.JpaUserRepository;
import sejong.coffee.yun.util.querydsl.UserQueryDslUtil;

import javax.persistence.EntityManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.*;
import static sejong.coffee.yun.domain.user.QMember.member;

@Repository
@Primary
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    @Override
    @Transactional
    public Member save(Member member) {
        return jpaUserRepository.save(member);
    }

    @Override
    public Member findById(Long id) {
        return getUser(id);
    }

    @Override
    public Member findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public List<Member> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jpaUserRepository.delete(getUser(id));
    }

    @Override
    public boolean exist(Long id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateName(Long id, String name) {
        Member member = getUser(id);

        member.updateName(name);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String password) {
        Member member = getUser(id);

        member.updatePassword(password);
    }

    @Override
    @Transactional
    public void updateEmail(Long id, String email) {
        Member member = getUser(id);

        member.updateEmail(email);
    }

    @Override
    public void duplicateEmail(String email) {
        if(jpaUserRepository.existsByEmail(email)) {
            throw DUPLICATE_USER_EMAIL.duplicatedException();
        }
    }

    @Override
    public void duplicateName(String name) {
        if(jpaUserRepository.existsByName(name)) {
            throw DUPLICATE_USER_NAME.duplicatedException();
        }
    }

    private Member getUser(Long id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(NOT_FOUND_USER::notFoundException);
    }

    @Override
    public void clear() {
        jpaUserRepository.deleteAll();
    }

    @Override
    @Transactional
    public void bulkInsert(List<Member> members) {

        String sql = "insert into member (id, create_at, update_at, city, detail, district, zip_code, email, total_price, user_name, order_count, password, user_rank, version, coupon_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, members.get(i).getId());
                ps.setNull(2, 0);
                ps.setNull(3, 0);
                ps.setString(4, members.get(i).getAddress().getCity());
                ps.setString(5, members.get(i).getAddress().getDetail());
                ps.setString(6, members.get(i).getAddress().getDistrict());
                ps.setString(7, members.get(i).getAddress().getZipCode());
                ps.setString(8, members.get(i).getEmail());
                ps.setBigDecimal(9, members.get(i).fetchTotalPrice());
                ps.setString(10, members.get(i).getName());
                ps.setInt(11, members.get(i).getOrderCount());
                ps.setString(12, members.get(i).getPassword());
                ps.setString(13, members.get(i).getUserRank().name());
                ps.setInt(14, members.get(i).getVersion());
                ps.setLong(15, members.get(i).getCoupon().getId());
            }

            @Override
            public int getBatchSize() {
                return members.size();
            }
        });
    }

    @Override
    @Transactional
    public Long userRankBulkUpdate(List<Long> ids, int orderCount, UserRank userRank) {
        long execute = jpaQueryFactory.update(member)
                .where(UserQueryDslUtil.updateCheckOrderCountAndUserRank(orderCount, userRank))
                .where(member.id.in(ids))
                .set(member.userRank, userRank)
                .execute();

        em.clear();
        em.flush();

        if(execute < 0) throw new RuntimeException("fail bulk update!!");

        return execute;
    }

    /**
     * 업데이트 유저 등급
     * @param orderCount 주문 개수
     * @param userRank 등급이 오를 유저 등급
     * @return 업데이트 수
     */
    @Override
    @Transactional
    public Long updateUserRank(int orderCount, UserRank userRank) {
        long execute = jpaQueryFactory.update(member)
                .where(UserQueryDslUtil.updateCheckOrderCountAndUserRank(orderCount, userRank))
                .set(member.userRank, userRank.upRank())
                .execute();

        em.clear();
        em.flush();

        if(execute < 0) throw new RuntimeException("fail bulk update!!");

        return execute;
    }

    @Override
    public List<Long> findAllUserId() {
        return jpaQueryFactory.select(member.id)
                .from(member)
                .fetch();
    }

    @Override
    @Transactional
    public void bulkDelete() {
        jpaQueryFactory.delete(member)
                .execute();

        em.clear();
        em.flush();
    }
}
