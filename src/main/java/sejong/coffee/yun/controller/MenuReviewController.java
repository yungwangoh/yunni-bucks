package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.dto.review.menu.MenuReviewDto;
import sejong.coffee.yun.dto.review.menu.MenuReviewPageDto;
import sejong.coffee.yun.dto.review.menu.MenuReviewPageWrapperDto;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.MenuReviewService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
@Slf4j
public class MenuReviewController {

    private final MenuReviewService menuReviewService;
    private final CustomMapper customMapper;

    @PostMapping("/{menuId}/reviews")
    ResponseEntity<MenuReviewDto.Response> menuReviewCreate(@RequestBody @Valid MenuReviewDto.Request request,
                                                            @MemberId Long memberId,
                                                            @PathVariable Long menuId) {

        MenuReview menuReview = menuReviewService.create(memberId, menuId, request.comment(), LocalDateTime.now());

        MenuReviewDto.Response response = customMapper.map(menuReview, MenuReviewDto.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reviews/{reviewId}")
    ResponseEntity<MenuReviewDto.Response> findMenuReview(@PathVariable Long reviewId) {

        MenuReview menuReview = menuReviewService.findReview(reviewId);

        MenuReviewDto.Response response = customMapper.map(menuReview, MenuReviewDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reviews/{reviewId}")
    ResponseEntity<MenuReviewDto.Update.Response> updateComment(@PathVariable Long reviewId,
                                                                @RequestBody @Valid MenuReviewDto.Request request,
                                                                @MemberId Long memberId) {

        MenuReview menuReview = menuReviewService.updateComment(memberId, reviewId, request.comment(), LocalDateTime.now());

        MenuReviewDto.Update.Response response = customMapper.map(menuReview, MenuReviewDto.Update.Response.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    ResponseEntity<Void> menuReviewDelete(@MemberId Long memberId,
                                          @PathVariable Long reviewId) {

        menuReviewService.delete(memberId, reviewId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews/page/{pageNum}")
    ResponseEntity<MenuReviewPageDto.Response> findAllByMemberId(@MemberId Long memberId,
                                                                 @PathVariable int pageNum) {
        PageRequest pr = PageRequest.of(pageNum, 10);

        Page<MenuReview> menuReviewPage = menuReviewService.findAllByMemberId(pr, memberId);

        MenuReviewPageDto.Response response = customMapper.map(menuReviewPage, MenuReviewPageDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{menuId}/reviews/page/{pageNum}")
    ResponseEntity<MenuReviewPageWrapperDto.PageResponse> findAllByMenuId(@PathVariable Long menuId,
                                                                          @PathVariable int pageNum) {

        PageRequest pr = PageRequest.of(pageNum, 10);

        MenuReviewPageWrapperDto.PageResponse pageResponse = menuReviewService.findAllByMenuId(pr, menuId);

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/reviews")
    ResponseEntity<List<MenuReviewDto.Response>> findAll() {
        List<MenuReview> menuReviewList = menuReviewService.findAll();

        List<MenuReviewDto.Response> responses = menuReviewList.stream().map(MenuReviewDto.Response::new).toList();

        return ResponseEntity.ok(responses);
    }
}
