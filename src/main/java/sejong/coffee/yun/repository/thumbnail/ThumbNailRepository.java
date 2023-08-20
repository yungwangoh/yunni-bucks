package sejong.coffee.yun.repository.thumbnail;

import sejong.coffee.yun.domain.order.menu.MenuThumbnail;

import java.util.List;

public interface ThumbNailRepository {

    MenuThumbnail save(MenuThumbnail menuThumbnail);
    List<MenuThumbnail> findAllByMenuId(Long menuId);
    MenuThumbnail findById(Long thumbnailId);
    void delete(Long thumbnailId);
}
