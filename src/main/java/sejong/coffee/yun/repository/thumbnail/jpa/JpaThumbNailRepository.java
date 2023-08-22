package sejong.coffee.yun.repository.thumbnail.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;

import java.util.List;
import java.util.Optional;

public interface JpaThumbNailRepository extends JpaRepository<MenuThumbnail, Long> {

    List<MenuThumbnail> findAllByMenuId(Long menuId);
    Optional<MenuThumbnail> findByMenuId(Long menuId);
    void deleteByMenuId(Long menuId);
}
