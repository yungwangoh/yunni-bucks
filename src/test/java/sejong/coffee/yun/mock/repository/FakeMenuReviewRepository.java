package sejong.coffee.yun.mock.repository;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.repository.review.menu.MenuReviewRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_REVIEW;

@TestComponent
public class FakeMenuReviewRepository implements MenuReviewRepository {

    private final List<MenuReview> reviews = new ArrayList<>();
    private Long id = 0L;

    @Override
    public MenuReview save(MenuReview menuReview) {
        if(menuReview.getId() == null || menuReview.getId() == 0L) {
            MenuReview newMenuReview = MenuReview.from(++id, menuReview);

            reviews.add(newMenuReview);

            return newMenuReview;
        }
        reviews.removeIf(mr -> Objects.equals(mr.getId(), menuReview.getId()));
        reviews.add(menuReview);
        return menuReview;
    }

    @Override
    public MenuReview findById(Long reviewId) {
        return reviews.stream()
                .filter(review -> Objects.equals(review.getId(), reviewId))
                .findAny()
                .orElseThrow(NOT_FOUND_MENU_REVIEW::notFoundException);
    }

    @Override
    public MenuReview findByMemberIdAndId(Long memberId, Long reviewId) {
        return reviews.stream()
                .filter(review -> Objects.equals(review.getMember().getId(), memberId))
                .filter(review -> Objects.equals(review.getId(), reviewId))
                .findAny()
                .orElseThrow(NOT_FOUND_MENU_REVIEW::notFoundException);
    }

    @Override
    public List<MenuReview> findAll() {
        return reviews;
    }

    @Override
    public void delete(Long reviewId) {
        MenuReview menuReview = reviews.stream()
                .filter(review -> Objects.equals(review.getId(), reviewId))
                .findAny()
                .orElseThrow(NOT_FOUND_MENU_REVIEW::notFoundException);

        reviews.remove(menuReview);
    }

    @Override
    public void delete(Long memberId, Long reviewId) {
        MenuReview menuReview = reviews.stream()
                .filter(review -> Objects.equals(review.getMember().getId(), memberId))
                .filter(review -> Objects.equals(review.getId(), reviewId))
                .findAny()
                .orElseThrow(NOT_FOUND_MENU_REVIEW::notFoundException);

        reviews.remove(menuReview);
    }

    @Override
    public Page<MenuReview> findAllByMemberId(Pageable pageable, Long memberId) {
        List<MenuReview> menuReviews = reviews.stream()
                .filter(review -> Objects.equals(review.getMember().getId(), memberId))
                .sorted(Comparator.comparing(MenuReview::getCreateAt).reversed())
                .toList();

        return new PageImpl<>(menuReviews, pageable, menuReviews.size());
    }
}
