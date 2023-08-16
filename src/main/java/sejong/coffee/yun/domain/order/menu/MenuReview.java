package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.user.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuReview extends DateTimeEntity {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "comments")
    private String comments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public MenuReview(String comments, Member member, Menu menu) {
        this.comments = comments;
        this.member = member;
        this.menu = menu;
    }

    private MenuReview(Long id, String comments, Member member, Menu menu) {
        this.id = id;
        this.comments = comments;
        this.member = member;
        this.menu = menu;
    }

    public static MenuReview from(Long id, MenuReview menuReview) {
        return new MenuReview(id, menuReview.getComments(), menuReview.getMember(), menuReview.getMenu());
    }
}
