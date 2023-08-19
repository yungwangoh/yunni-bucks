package sejong.coffee.yun.repository.review.menu.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.menu.MenuReview;

public interface JpaMenuReviewRepository extends JpaRepository<MenuReview, Long> {
}
