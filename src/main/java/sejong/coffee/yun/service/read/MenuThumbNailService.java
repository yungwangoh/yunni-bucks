package sejong.coffee.yun.service.read;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MenuThumbNailService {
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

    public void updateThumbnail(MultipartFile multipartFile, Long menuId, LocalDateTime updateAt) {
        MenuThumbnail menuThumbnail = thumbNailRepository.findByMenuId(menuId);

        uploadFile(multipartFile, menuThumbnail.getStoredFileName());

        menuThumbnail.setUpdateAt(updateAt);
    }

    private void uploadFile(MultipartFile multipartFile, String uploadFilePath) {

        try(FileOutputStream fileOutputStream = new FileOutputStream(uploadFilePath, false)) {

            Thumbnailator.createThumbnail(multipartFile.getInputStream(), fileOutputStream, 100, 100);

        } catch (Exception e) {

            log.error("upload error = {}", e.getMessage());
            throw new RuntimeException("file upload error");
        }
    }

    private String getFileExtension(String fileName) {
        int indexOf = fileName.indexOf(".");

        if(indexOf > -1) {
            return fileName.substring(fileName.indexOf("."));
        }

        return "";
    }

    private void mkdir(File file) {
        if (!file.exists()) {
            if(file.mkdir()) {
                log.info("success create dir!!");
            } else {
                log.error("error create dir!!");
            }
        }
    }

    private void checkImageFileFormat(MultipartFile multipartFile) {
        try {
            String contentType = multipartFile.getContentType();

            if(contentType == null || !contentType.startsWith("image")) {
                throw new RuntimeException("not image");
            }

        } catch (Exception e) {
            log.error("image error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
