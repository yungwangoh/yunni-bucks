package sejong.coffee.yun.dto.thumbnail;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;

import java.io.FileInputStream;
import java.io.IOException;

import static sejong.coffee.yun.service.PathControl.PATH;

public class ThumbNailDto {
    public record Response(Long menuId, Resource resource) {
        public Response(MenuThumbnail thumbnail) throws IOException {
            this(
                    thumbnail.getMenu().getId(),
                    new InputStreamResource(new FileInputStream(PATH.getPath() + thumbnail.getOriginFileName()))
            );
        }
    }
}
