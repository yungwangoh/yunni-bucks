package sejong.coffee.yun.service;

import org.springframework.web.multipart.MultipartFile;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;

import java.time.LocalDateTime;
import java.util.List;

public interface MenuThumbNailService {

    default MenuThumbnail create(MultipartFile multipartFile, Long menuId, LocalDateTime now) {
        return null;
    }
    default void updateThumbnail(MultipartFile multipartFile, Long menuId, LocalDateTime updateAt) {}
    default void delete(Long thumbNailId) {}
    default void deleteByMenuId(Long menuId) {}
    default List<MenuThumbnail> findAllByMenuId(Long menuId) {
        return null;
    }
    default MenuThumbnail findById(Long thumbNailId) {
        return null;
    }
    default MenuThumbnail findByMenuId(Long menuId) {
        return null;
    }
}
