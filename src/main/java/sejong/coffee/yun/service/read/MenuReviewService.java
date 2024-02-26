package sejong.coffee.yun.service.read;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.dto.review.menu.MenuReviewPageWrapperDto;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.review.menu.MenuReviewRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuReviewService {

    private final UserRepository userRepository;
    private final MenuReviewRepository menuReviewRepository;
    private final MenuRepository menuRepository;

    public MenuReview findReview(Long reviewId) {
        return menuReviewRepository.findById(reviewId);
    }

    public List<MenuReview> findAll() {
        return menuReviewRepository.findAll();
    }

    public List<MenuReview> findByComments(String searchComment) {
        return menuReviewRepository.findByCommentsContaining(searchComment);
    }

    public List<MenuReview> findByFullTextComment(String searchComment) {
        return menuReviewRepository.fullTextSearchComments(searchComment);
    }

    public List<MenuReview> findByFullTextCommentsNative(String searchComment) {
        return menuReviewRepository.fullTextSearchCommentsNative(searchComment);
    }

    public Page<MenuReview> findAllByMemberId(Pageable pageable, Long memberId) {
        return menuReviewRepository.findAllByMemberId(pageable, memberId);
    }

    @Cacheable(value = "menuReview", key = "#pageable.pageNumber", cacheManager = "cacheManager")
    public MenuReviewPageWrapperDto.PageResponse findAllByMenuId(Pageable pageable, Long menuId) {

        Page<MenuReview> reviews = menuReviewRepository.findAllByMenuId(pageable, menuId);

        return new MenuReviewPageWrapperDto.PageResponse(reviews);
    }
}
