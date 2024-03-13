package sejong.coffee.yun.repository.user;

import org.springframework.data.redis.core.RedisOperations;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.UserRank;

import java.util.List;

public interface UserRepository {

    Long increaseOrderCount(RedisOperations<String, String> redisOperations, Long memberId, int menuCount);
    Long decreaseOrderCount(RedisOperations<String, String> redisOperations, Long memberId, int menuCount);
    Member save(Member member);
    Member findById(Long id);
    List<Member> findAll();
    boolean exist(Long id);
    void delete(Long id);
    void updateName(Long id, String name);
    void updatePassword(Long id, String password);
    void updateEmail(Long id, String email);
    void duplicateEmail(String email);
    void duplicateName(String name);
    Member findByEmail(String email);
    void clear();
    default Long updateUserRank(int orderCount, UserRank updateUserRank) { return null; }
    default void bulkInsert(List<Member> members) {}
    @Deprecated
    default Long userRankBulkUpdate(List<Long> ids, int orderCount, UserRank userRank) {return null;}
    @Deprecated
    default List<Long> findAllUserId() {return null;}
    default void bulkDelete() {}
}
