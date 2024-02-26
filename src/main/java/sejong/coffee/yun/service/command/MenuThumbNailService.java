package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static sejong.coffee.yun.util.path.PathControl.PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuThumbNailService {
    private final ThumbNailRepository thumbNailRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public MenuThumbnail create(MultipartFile multipartFile, Long menuId, LocalDateTime now) {

        File file = new File(PATH.getPath());

        checkImageFileFormat(multipartFile);

        mkdir(file);

        String originFileName = Objects.requireNonNull(multipartFile.getOriginalFilename(), "file is null!!");
        String storedFilename = PATH.getPath() + UUID.randomUUID() + getFileExtension(originFileName);

        uploadFile(multipartFile, storedFilename);

        Menu menu = menuRepository.findById(menuId);

        MenuThumbnail menuThumbnail = MenuThumbnail.create(menu, originFileName, storedFilename, now);

        return thumbNailRepository.save(menuThumbnail);
    }

    @Deprecated
    public List<MenuThumbnail> findAllByMenuId(Long menuId) {
        return thumbNailRepository.findAllByMenuId(menuId);
    }

    @Deprecated
    public MenuThumbnail findById(Long thumbNailId) {
        return thumbNailRepository.findById(thumbNailId);
    }

    @Deprecated
    public MenuThumbnail findByMenuId(Long menuId) {
        return thumbNailRepository.findByMenuId(menuId);
    }

    public void updateThumbnail(MultipartFile multipartFile, Long menuId, LocalDateTime updateAt) {
        MenuThumbnail menuThumbnail = thumbNailRepository.findByMenuId(menuId);

        uploadFile(multipartFile, menuThumbnail.getStoredFileName());

        menuThumbnail.setUpdateAt(updateAt);
    }

    @Transactional
    public void delete(Long thumbNailId) {
        thumbNailRepository.delete(thumbNailId);
    }

    @Transactional
    public void deleteByMenuId(Long menuId) {
        thumbNailRepository.deleteByMenuId(menuId);
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
