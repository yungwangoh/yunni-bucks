package sejong.coffee.yun.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;
import sejong.coffee.yun.service.MenuThumbNailService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MenuThumbNailServiceQuery implements MenuThumbNailService {
    private final ThumbNailRepository thumbNailRepository;

    public List<MenuThumbnail> findAllByMenuId(Long menuId) {
        return thumbNailRepository.findAllByMenuId(menuId);
    }

    public MenuThumbnail findById(Long thumbNailId) {
        return thumbNailRepository.findById(thumbNailId);
    }

    public MenuThumbnail findByMenuId(Long menuId) {
        return thumbNailRepository.findByMenuId(menuId);
    }
}
