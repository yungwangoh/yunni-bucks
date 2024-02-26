package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.review.menu.MenuReviewRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.MenuReviewService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuReviewServiceCommand implements MenuReviewService {

    private final UserRepository userRepository;
    private final MenuReviewRepository menuReviewRepository;
    private final MenuRepository menuRepository;

    public MenuReview create(Long memberId, Long menuId, String comment, LocalDateTime now) {
        Member member = userRepository.findById(memberId);

        Menu menu = menuRepository.findById(menuId);

        MenuReview menuReview = MenuReview.create(comment, member, menu, now);

        return menuReviewRepository.save(menuReview);
    }

    @CacheEvict(value = "menuReview", key = "#reviewId", cacheManager = "cacheManager")
    public void delete(Long reviewId) {
        menuReviewRepository.delete(reviewId);
    }

    @CacheEvict(value = "menuReview", key = "#memberId + #reviewId", cacheManager = "cacheManager")
    public void delete(Long memberId, Long reviewId) {
        menuReviewRepository.delete(memberId, reviewId);
    }

    @CachePut(value = "menuReview", key = "#memberId + #reviewId + #comments")
    public MenuReview updateComment(Long memberId, Long reviewId, String comments, LocalDateTime now) {
        MenuReview menuReview = menuReviewRepository.findByMemberIdAndId(memberId, reviewId);

        menuReview.updateComment(comments);

        menuReview.setUpdateAt(now);

        return menuReview;
    }
}
