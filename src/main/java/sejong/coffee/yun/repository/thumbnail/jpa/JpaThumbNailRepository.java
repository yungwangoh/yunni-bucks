package sejong.coffee.yun.repository.thumbnail.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;

import java.util.List;

public interface JpaThumbNailRepository extends JpaRepository<MenuThumbnail, Long> {

    List<MenuThumbnail> findAllByMenuId(Long menuId);
}
