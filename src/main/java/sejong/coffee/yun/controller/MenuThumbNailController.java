package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sejong.coffee.yun.domain.order.menu.MenuThumbnail;
import sejong.coffee.yun.service.MenuThumbNailService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import static sejong.coffee.yun.service.PathControl.PATH;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/api")
public class MenuThumbNailController {

    private final MenuThumbNailService menuThumbNailService;

    @PostMapping("/{menuId}/thumbnails")
    ResponseEntity<Void> upload(@RequestPart("image") MultipartFile multipartFile, @PathVariable Long menuId) {

        menuThumbNailService.create(multipartFile, menuId, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{menuId}/thumbnails")
    ResponseEntity<Resource> findThumbNailByMenuId(@PathVariable Long menuId) throws FileNotFoundException {

        MenuThumbnail menuThumbnails = menuThumbNailService.findByMenuId(menuId);

        Resource resource = new InputStreamResource(new FileInputStream(PATH.getPath() + menuThumbnails.getOriginFileName()));

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/thumbnails/{thumbnailId}")
    ResponseEntity<Void> delete(@PathVariable Long thumbnailId) {
        menuThumbNailService.delete(thumbnailId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{menuId}/thumbnails")
    ResponseEntity<Void> deleteByMenuId(@PathVariable Long menuId) {
        menuThumbNailService.deleteByMenuId(menuId);

        return ResponseEntity.noContent().build();
    }
}
