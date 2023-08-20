package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuThumbnail {

    @Id @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
    private String originFileName;
    private String storedFileName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @Builder
    private MenuThumbnail(Long id, Menu menu, String originFileName, String storedFileName, LocalDateTime createAt, LocalDateTime updateAt) {
        this.id = id;
        this.menu = menu;
        this.originFileName = originFileName;
        this.storedFileName = storedFileName;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public static MenuThumbnail create(Menu menu, String originFileName, String storedFileName, LocalDateTime now) {
        return MenuThumbnail.builder()
                .menu(menu)
                .createAt(now)
                .updateAt(now)
                .originFileName(originFileName)
                .storedFileName(storedFileName)
                .build();
    }

    public static MenuThumbnail from(Long id, MenuThumbnail menuThumbnail) {
        return MenuThumbnail.builder()
                .id(id)
                .menu(menuThumbnail.getMenu())
                .updateAt(menuThumbnail.getUpdateAt())
                .createAt(menuThumbnail.getCreateAt())
                .storedFileName(menuThumbnail.getStoredFileName())
                .originFileName(menuThumbnail.getOriginFileName())
                .build();
    }

    public void setUpdateAt(LocalDateTime now) {
        this.updateAt = now;
    }
}
