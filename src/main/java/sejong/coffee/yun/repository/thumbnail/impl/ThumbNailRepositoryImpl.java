package sejong.coffee.yun.repository.thumbnail.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;
import sejong.coffee.yun.repository.thumbnail.jpa.JpaThumbNailRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_THUMBNAIL;

@Repository
@Primary
@RequiredArgsConstructor
public class ThumbNailRepositoryImpl implements ThumbNailRepository {

    private final JpaThumbNailRepository jpaThumbNailRepository;

    @Override
    @Transactional
    public MenuThumbnail save(MenuThumbnail menuThumbnail) {
        return jpaThumbNailRepository.save(menuThumbnail);
    }

    @Override
    public List<MenuThumbnail> findAllByMenuId(Long menuId) {
        return jpaThumbNailRepository.findAllByMenuId(menuId);
    }

    @Override
    public MenuThumbnail findById(Long thumbnailId) {
        return jpaThumbNailRepository.findById(thumbnailId)
                .orElseThrow(NOT_FOUND_MENU_THUMBNAIL::notFoundException);
    }

    @Override
    public MenuThumbnail findByMenuId(Long menuId) {
        return jpaThumbNailRepository.findByMenuId(menuId)
                .orElseThrow(NOT_FOUND_MENU_THUMBNAIL::notFoundException);
    }

    @Override
    @Transactional
    public void delete(Long thumbnailId) {
        jpaThumbNailRepository.deleteById(thumbnailId);
    }


    @Override
    @Transactional
    public void deleteByMenuId(Long menuId) {
        jpaThumbNailRepository.deleteByMenuId(menuId);
    }
}
