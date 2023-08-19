package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.user.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuReview {

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
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private MenuReview(String comments, Member member, Menu menu, LocalDateTime now) {
        this.comments = comments;
        this.member = member;
        this.menu = menu;
        this.createAt = now;
        this.updateAt = now;
    }

    private MenuReview(Long id, String comments, Member member, Menu menu, LocalDateTime now) {
        this.id = id;
        this.comments = comments;
        this.member = member;
        this.menu = menu;
        this.createAt = now;
        this.updateAt = now;
    }

    public static MenuReview create(String comments, Member member, Menu menu, LocalDateTime now) {
        return new MenuReview(comments, member, menu, now);
    }

    public static MenuReview from(Long id, MenuReview menuReview) {
        return new MenuReview(id, menuReview.getComments(), menuReview.getMember(), menuReview.getMenu(), menuReview.getCreateAt());
    }

    public void updateComment(String comments) {
        this.comments = comments;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
