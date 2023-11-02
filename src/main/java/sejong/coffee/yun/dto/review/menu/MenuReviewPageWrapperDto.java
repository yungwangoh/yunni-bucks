package sejong.coffee.yun.dto.review.menu;

import org.springframework.data.domain.Page;
import sejong.coffee.yun.domain.order.menu.MenuReview;

import java.util.List;

public class MenuReviewPageWrapperDto {

    public record PageResponse(int pageNum, List<MenuReviewDto.Response> responses) {

        public PageResponse(Page<MenuReview> responses) {
            this(
                    responses.getNumber(),
                    responses.stream().map(MenuReviewDto.Response::new).toList()
            );
        }
    }
}
