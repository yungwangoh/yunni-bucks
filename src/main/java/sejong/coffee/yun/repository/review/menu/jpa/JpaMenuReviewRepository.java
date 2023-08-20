package sejong.coffee.yun.repository.review.menu.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.menu.MenuReview;

import java.util.Optional;

public interface JpaMenuReviewRepository extends JpaRepository<MenuReview, Long> {
    void deleteByMemberIdAndId(Long memberId, Long reviewId);
    Optional<MenuReview> findByMemberIdAndId(Long memberId, Long reviewId);
}
