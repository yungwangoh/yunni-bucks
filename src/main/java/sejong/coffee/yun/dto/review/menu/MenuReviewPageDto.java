package sejong.coffee.yun.dto.review.menu;

import org.springframework.data.domain.Page;
import sejong.coffee.yun.domain.order.menu.MenuReview;

import java.util.List;

public class MenuReviewPageDto {
    public record Response(int pageNum, List<MenuReviewDto.Response> responses) {
        public Response(Page<MenuReview> page) {
            this(page.getNumber(), page.getContent().stream().map(MenuReviewDto.Response::new).toList());
        }
    }
}
