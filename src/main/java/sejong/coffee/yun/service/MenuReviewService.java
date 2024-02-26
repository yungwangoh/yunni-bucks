package sejong.coffee.yun.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.dto.review.menu.MenuReviewPageWrapperDto;

import java.time.LocalDateTime;
import java.util.List;

public interface MenuReviewService {

    default MenuReview create(Long memberId, Long menuId, String comment, LocalDateTime now) {
        return null;
    }
    default void delete(Long reviewId) {}
    default void delete(Long memberId, Long reviewId) {}
    default MenuReview updateComment(Long memberId, Long reviewId, String comments, LocalDateTime now) {
        return null;
    }
    default MenuReview findReview(Long reviewId) {
        return null;
    }
    default List<MenuReview> findAll() {
        return null;
    }
    default List<MenuReview> findByComments(String searchComment) {
        return null;
    }
    default List<MenuReview> findByFullTextComment(String searchComment) {
        return null;
    }
    default List<MenuReview> findByFullTextCommentsNative(String searchComment) {
        return null;
    }
    default Page<MenuReview> findAllByMemberId(Pageable pageable, Long memberId) {
        return null;
    }
    default MenuReviewPageWrapperDto.PageResponse findAllByMenuId(Pageable pageable, Long menuId) {
        return null;
    }
}
