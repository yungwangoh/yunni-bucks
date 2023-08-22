package sejong.coffee.yun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.dto.thumbnail.ThumbNailDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.MenuThumbNailService;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sejong.coffee.yun.service.PathControl.PATH;

@WebMvcTest(MenuThumbNailController.class)
class MenuThumbNailControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MenuThumbNailService menuThumbNailService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private CustomMapper customMapper;

    static MenuThumbnail menuThumbnail;
    static ThumbNailDto.Response response;
    static Menu menu;
    static String token = "bearer accessToken";
    static MockMultipartFile multipartFile;

    @BeforeAll
    static void init() throws Exception {
        String name = "image";
        String originalFileName = "test.jpeg";
        String fileUrl = "/Users/yungwang-o/Documents/test.jpeg";

        multipartFile = new MockMultipartFile(name, originalFileName, MediaType.IMAGE_JPEG_VALUE, new FileInputStream(fileUrl));

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        String origin = "test.jpeg";
        String stored = UUID.randomUUID() + "_" + origin;

        menuThumbnail = MenuThumbnail.create(menu, origin, stored, LocalDateTime.now());
    }

    @Test
    void 썸네일_저장_업로드_201() throws Exception {
        // given
        given(menuThumbNailService.create(any(), anyLong(), any())).willReturn(menuThumbnail);

        // when
        ResultActions resultActions = mockMvc.perform(multipart("/api/{menuId}/thumbnails", 1L)
                .file(multipartFile)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void 썸네일_업로드_실패_멀티파트_파일을_찾지_못할때_400() throws Exception {
        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test", // part name not match
                "test.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "test".getBytes(StandardCharsets.UTF_8)
        );

        given(menuThumbNailService.create(any(), anyLong(), any())).willReturn(menuThumbnail);

        // when
        ResultActions resultActions = mockMvc.perform(multipart("/api/{menuId}/thumbnails", 1L)
                .file(mockMultipartFile)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 썸네일_찾기_다운로드_200() throws Exception {
        // given
        given(menuThumbNailService.findByMenuId(anyLong())).willReturn(menuThumbnail);
        byte[] bytes = Files.readAllBytes(Paths.get(PATH.getPath() + menuThumbnail.getOriginFileName()));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/{menuId}/thumbnails", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(bytes));

    }

    @Test
    void 썸네일_삭제_204() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/{menuId}/thumbnails", 1L));

        // then
        resultActions.andExpect(status().isNoContent());
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}