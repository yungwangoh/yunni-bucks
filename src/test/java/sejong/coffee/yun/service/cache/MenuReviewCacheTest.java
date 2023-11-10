package sejong.coffee.yun.service.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.MenuReviewService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class MenuReviewCacheTest extends MainIntegrationTest {

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuReviewService menuReviewService;
    @Autowired
    private UserRepository userRepository;

    private Menu menu;
    private Member member;

    @PostConstruct
    void init() {
        member = userRepository.save(member());
        menu = menuRepository.save(bread());

        int reviewCount = 100;
        String reviewComment = "맛있어요!";

        List<MenuReview> menuReviews = Stream.generate(() -> menuReviewService.create(member.getId(), menu.getId(), reviewComment, LocalDateTime.now()))
                .limit(reviewCount)
                .toList();
    }

    @Test
    void 메뉴_리뷰_조회_캐시() {
        PageRequest pr = PageRequest.of(0, 10);

        for(int i = 0; i < 1000; i++) {
            menuReviewService.findAllByMenuId(pr, menu.getId());
        }

    }
}
