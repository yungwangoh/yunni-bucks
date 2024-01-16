package sejong.coffee.yun.repository.review.menu.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sejong.coffee.yun.domain.order.menu.MenuReview;

import java.util.List;
import java.util.Optional;

public interface JpaMenuReviewRepository extends JpaRepository<MenuReview, Long> {
    void deleteByMemberIdAndId(Long memberId, Long reviewId);

    @Query("select mr from MenuReview mr join fetch mr.member m join fetch mr.menu where m.id = :memberId and mr.id = :reviewId")
    Optional<MenuReview> findByMemberIdAndId(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);

    List<MenuReview> findByCommentsContaining(String searchComment);

    @Query(value = "SELECT * FROM menu_review WHERE MATCH (comments) AGAINST (?1 IN BOOLEAN MODE )", nativeQuery = true)
    List<MenuReview> searchFullTextComments(String keyword);
}
