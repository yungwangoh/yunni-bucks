package sejong.coffee.yun.dto.review.menu;

import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.domain.user.UserRank;

import javax.validation.constraints.NotBlank;

public class MenuReviewDto {
    public record Request(@NotBlank String comment) {}
    public record Response(Long id, String userName, UserRank userRank, String comment) {
        public Response(MenuReview review) {
            this(
                    review.getId(),
                    review.getMember().getName(),
                    review.getMember().getUserRank(),
                    review.getComments()
            );
        }
    }
}
