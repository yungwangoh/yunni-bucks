package sejong.coffee.yun.integration.menu;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;
import sejong.coffee.yun.service.MenuThumbNailService;

import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuThumbnailIntegrationTest extends MainIntegrationTest {

    @Autowired
    private MenuThumbNailService menuThumbNailService;
    @Autowired
    private ThumbNailRepository thumbNailRepository;

    @AfterEach
    void initDB() {
        thumbNailRepository.clear();
    }

    @Nested
    @DisplayName("썸네일을 업로드한다.")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class Upload {

        MockMultipartFile multipartFile;

        @BeforeEach
        void init() throws Exception {
            String name = "image";
            String originalFileName = "test.jpeg";
            String fileUrl = "/Users/yungwang-o/Documents/test.jpeg";

            multipartFile = new MockMultipartFile(
                    name,
                    originalFileName,
                    MediaType.IMAGE_JPEG_VALUE,
                    new FileInputStream(fileUrl));
        }

        @Test
        void 썸네일_업로드_204() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails-upload", 1L)
                    .file(multipartFile));

            // then
            resultActions.andExpect(status().isNoContent())
                    .andDo(document("thumbnail-upload",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            requestParts(
                                    partWithName("image").description("썸네일 이미지")
                            )
                    ));
        }

        @Test
        void 이미지가_아닌_다른_파일을_업로드할_경우_500() throws Exception {
            // given
            String name = "image";
            String originalFileName = "test.jpeg";
            String fileUrl = "/Users/yungwang-o/Documents/test.jpeg";
            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    name,
                    originalFileName,
                    MediaType.APPLICATION_JSON_VALUE,
                    new FileInputStream(fileUrl)
            );

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails-upload", 1L)
                    .file(mockMultipartFile));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("thumbnail-upload-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            requestParts(
                                    partWithName("image").description("썸네일 이미지")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 메뉴에_업로드_된_이미지_가져오기_200() throws Exception {
            // given
            menuThumbNailService.create(multipartFile, 1L, LocalDateTime.now());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails", 1L));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("file-download",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            responseBody()
                    ));
        }

        @Test
        void 잘못된_메뉴_ID를_요청했을_경우_500() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails-upload", 100L)
                    .file(multipartFile));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("thumbnail-upload-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            requestParts(
                                    partWithName("image").description("썸네일 이미지")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 메뉴_썸네일_수정_204() throws Exception {
            // given
            menuThumbNailService.create(multipartFile, 1L, LocalDateTime.now());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails-edit", 1L)
                    .file(multipartFile));

            // then
            resultActions.andExpect(status().isNoContent())
                    .andDo(document("thumbnail-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            requestParts(
                                    partWithName("image").description("썸네일 이미지")
                            )
                    ));
        }
    }
}
