package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.FakeMenuRepository;
import sejong.coffee.yun.mock.repository.FakeMenuReviewRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.MenuReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        MenuReviewService.class,
        FakeMenuRepository.class,
        FakeUserRepository.class,
        FakeMenuReviewRepository.class,
        JwtProvider.class,
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList"
})
public class MenuReviewServiceTest {

    @Autowired
    private MenuReviewService menuReviewService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FakeMenuRepository fakeMenuRepository;

    Member saveMember;
    Menu saveMenu;

    @BeforeEach
    void init() {
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        saveMember = userRepository.save(member);

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        Menu menu = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        saveMenu = menuRepository.save(menu);
    }

    @AfterEach
    void initDB() {
        fakeUserRepository.clear();
        fakeMenuRepository.clear();
    }

    @Test
    void 메뉴_리뷰_저장() {
        // given
        String review = "맛있어요";

        // when
        MenuReview menuReview = menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        // then
        assertThat(menuReview.getComments()).isEqualTo(review);
        assertThat(menuReview.getMenu()).isEqualTo(saveMenu);
        assertThat(menuReview.getMember()).isEqualTo(saveMember);
    }

    @Test
    void 메뉴_리뷰_찾기() {
        // given
        String review = "맛있어요";

        MenuReview menuReview = menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        // when
        MenuReview findReview = menuReviewService.findReview(menuReview.getId());

        // then
        assertThat(findReview).isEqualTo(menuReview);
    }

    @Test
    void 메뉴_리뷰_삭제() {
        // given
        String review = "맛있어요";

        MenuReview menuReview = menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        // when
        menuReviewService.delete(menuReview.getId());

        // then
        assertThatThrownBy(() -> menuReviewService.findReview(menuReview.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU_REVIEW.getMessage());
    }

    @Test
    void 메뉴_리뷰_삭제_실패_잘못된_회원ID() {
        // given
        String review = "맛있어요";

        MenuReview menuReview = menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        // when

        // then
        assertThatThrownBy(() -> menuReviewService.delete(100L, menuReview.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU_REVIEW.getMessage());
    }

    @Test
    void 메뉴_리뷰_삭제_실패_잘못된_리뷰ID() {
        // given
        String review = "맛있어요";

        menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        // when

        // then
        assertThatThrownBy(() -> menuReviewService.delete(saveMember.getId(), 100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU_REVIEW.getMessage());
    }

    @Test
    void 메뉴_리뷰_코멘트_수정() {
        // given
        String review = "맛있어요";
        String updateReview = "맛없어";

        MenuReview menuReview = menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        LocalDateTime updateTime = LocalDateTime.of(2022, 11, 20, 11, 20);

        // when
        MenuReview updateComment = menuReviewService.updateComment(saveMember.getId(), menuReview.getId(), updateReview, updateTime);

        // then
        assertThat(updateComment.getComments()).isEqualTo(updateReview);
        assertThat(menuReview.getUpdateAt()).isEqualTo(updateTime);
    }

    @Test
    void 유저가_쓴_리뷰_내역() {
        // given
        int size = 10;
        String review = "맛있어요";

        IntStream.range(0, size).forEach(i -> menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now()));

        PageRequest pr = PageRequest.of(0, 10);

        // when
        Page<MenuReview> reviewPage = menuReviewService.findAllByMemberId(pr, saveMember.getId());

        // then
        assertThat(reviewPage.getContent().size()).isEqualTo(size);
        assertThat(reviewPage.getTotalElements()).isEqualTo(size);
    }

    @Test
    void 페이징_유저_찾기_실패() {
        // given
        int size = 10;
        String review = "맛있어요";

        IntStream.range(0, size).forEach(i -> menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now()));

        PageRequest pr = PageRequest.of(0, 10);

        // when
        Page<MenuReview> menuReviewPage = menuReviewService.findAllByMemberId(pr, 100L);

        // then
        assertThat(menuReviewPage.getContent()).isEqualTo(List.of());
    }

    @Test
    void 메뉴_리뷰_찾기_실패() {
        // given
        String review = "맛있어요";

        menuReviewService.create(saveMember.getId(), saveMenu.getId(), review, LocalDateTime.now());

        // when

        // then
        assertThatThrownBy(() -> menuReviewService.findReview(10L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU_REVIEW.getMessage());
    }

    @Test
    void 잘못된_메뉴_id() {
        // given
        String review = "맛있어요";

        // when

        // then
        assertThatThrownBy(() -> menuReviewService.create(saveMember.getId(), 10L, review, LocalDateTime.now()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU.getMessage());
    }

    @Test
    void 잘못된_회원_id() {
        // given
        String review = "맛있어요";

        // when

        // then
        assertThatThrownBy(() -> menuReviewService.create(10L, saveMenu.getId(), review, LocalDateTime.now()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }
}
