package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.mock.repository.FakeMenuRepository;
import sejong.coffee.yun.mock.repository.FakeMeuThumbNeilRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;
import sejong.coffee.yun.service.command.MenuThumbNailService;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_THUMBNAIL;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        MenuThumbNailService.class,
        FakeMeuThumbNeilRepository.class,
        FakeMenuRepository.class,
        JwtProvider.class,
        CustomMapper.class
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList"
})
class MenuThumbNailServiceTest {

    @Autowired
    private MenuThumbNailService menuThumbNailService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ThumbNailRepository thumbNailRepository;
    @Autowired
    private FakeMenuRepository fakeMenuRepository;
    @Autowired
    private FakeMeuThumbNeilRepository fakeMeuThumbNeilRepository;
    @Autowired
    private CustomMapper customMapper;

    Menu saveMenu;

    @BeforeEach
    void init() {
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        Beverage beverage = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        saveMenu = menuRepository.save(beverage);
    }

    @AfterEach
    void initDB() {
        fakeMenuRepository.clear();
        fakeMeuThumbNeilRepository.clear();
    }

    @Test
    void 썸네일_저장() throws Exception {
        // given
        String name = "image";
        String originalFileName = "test.jpeg";
        String fileUrl = "/Users/yungwang-o/Documents/test.jpeg";

        MockMultipartFile multipartFile = new MockMultipartFile(
                name,
                originalFileName,
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(fileUrl));

        // when
        MenuThumbnail menuThumbnail = menuThumbNailService.create(multipartFile, saveMenu.getId(), LocalDateTime.now());

        // then
        assertThat(menuThumbnail.getOriginFileName()).isEqualTo(originalFileName);
    }

    @Test
    void 썸네일_저장_NPE() {
        assertThatThrownBy(() -> menuThumbNailService.create(null, saveMenu.getId(), LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 썸네일_저장_시간() throws Exception {
        // given
        String name = "image";
        String originalFileName = "test.jpeg";
        String contentType = "image/jpeg";
        String fileUrl = "/Users/yungwang-o/Documents/test.jpeg";

        LocalDateTime createAt = LocalDateTime.of(2022, 5, 30, 2, 30);
        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, new FileInputStream(fileUrl));

        // when
        MenuThumbnail menuThumbnail = menuThumbNailService.create(multipartFile, saveMenu.getId(), createAt);

        // then
        assertThat(menuThumbnail.getCreateAt()).isEqualTo(createAt);
    }

    @Test
    void 썸네일_찾기() {
        // given
        String origin = "test.jpg";
        String stored = UUID.randomUUID() + "_" + origin;

        MenuThumbnail menuThumbnail = MenuThumbnail.create(saveMenu, origin, stored, LocalDateTime.now());
        MenuThumbnail saveThumbNail = thumbNailRepository.save(menuThumbnail);

        // when
        List<MenuThumbnail> thumbnails = menuThumbNailService.findAllByMenuId(saveMenu.getId());

        // then
        assertThat(thumbnails.get(0)).isEqualTo(saveThumbNail);
    }

    @Test
    void 썸네일_찾기_잘못된_ID() {
        // given
        String origin = "test.jpg";
        String stored = UUID.randomUUID() + "_" + origin;

        MenuThumbnail menuThumbnail = MenuThumbnail.create(saveMenu, origin, stored, LocalDateTime.now());
        thumbNailRepository.save(menuThumbnail);

        // when

        // then
        assertThatThrownBy(() -> menuThumbNailService.findById(100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU_THUMBNAIL.getMessage());
    }

    @Test
    void 썸네일_삭제() {
        // given
        String origin = "test.jpg";
        String stored = UUID.randomUUID() + "_" + origin;

        MenuThumbnail menuThumbnail = MenuThumbnail.create(saveMenu, origin, stored, LocalDateTime.now());
        MenuThumbnail saveThumbNail = thumbNailRepository.save(menuThumbnail);

        // when
        menuThumbNailService.delete(saveThumbNail.getId());

        // then
        assertThatThrownBy(() -> menuThumbNailService.findById(saveThumbNail.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU_THUMBNAIL.getMessage());
    }

    @Test
    void 썸네일_메뉴ID로_찾기() {
        // given
        String origin = "test.jpg";
        String stored = UUID.randomUUID() + "_" + origin;

        MenuThumbnail menuThumbnail = MenuThumbnail.create(saveMenu, origin, stored, LocalDateTime.now());
        MenuThumbnail saveThumbNail = thumbNailRepository.save(menuThumbnail);

        List<MenuThumbnail> menuThumbnails = List.of(saveThumbNail);

        // when
        List<MenuThumbnail> thumbnails = menuThumbNailService.findAllByMenuId(saveMenu.getId());

        // then
        assertThat(thumbnails).isEqualTo(menuThumbnails);
    }
}