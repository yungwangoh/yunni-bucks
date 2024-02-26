package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Bread;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.service.command.MenuServiceCommand;
import sejong.coffee.yun.service.query.MenuServiceQuery;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
@Validated
@Slf4j
public class MenuController {

    private final MenuServiceCommand menuServiceCommand;
    private final MenuServiceQuery menuServiceQuery;

    @PostMapping("/bread")
    public ResponseEntity<MenuDto.Response> breadCreate(@RequestBody @Valid MenuDto.Request request) {

        Menu menu = Bread.builder()
                .description(request.description())
                .menuSize(request.menuSize())
                .quantity(request.quantity())
                .price(request.price())
                .nutrients(request.nutrients())
                .title(request.title())
                .now(LocalDateTime.now())
                .build();

        Menu createMenu = menuServiceCommand.create(menu);

        return new ResponseEntity<>(new MenuDto.Response(createMenu), HttpStatus.CREATED);
    }

    @PostMapping("/beverage")
    public ResponseEntity<MenuDto.Response> beverageCreate(@RequestBody @Valid MenuDto.Request request) {

        Menu menu = Beverage.builder()
                .description(request.description())
                .menuSize(request.menuSize())
                .quantity(request.quantity())
                .price(request.price())
                .nutrients(request.nutrients())
                .title(request.title())
                .now(LocalDateTime.now())
                .build();

        Menu createMenu = menuServiceCommand.create(menu);

        return new ResponseEntity<>(new MenuDto.Response(createMenu), HttpStatus.CREATED);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDto.Response> findMenu(@PathVariable Long menuId) {

        MenuDto.Response response = menuServiceQuery.findById(menuId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<List<MenuDto.Response>> findAllMenu() {

        List<MenuDto.Response> responses = menuServiceQuery.findAll().responses();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<MenuDto.Response>> searchPopularMenus() {

        List<MenuDto.Response> responses = menuServiceQuery.searchPopularMenus();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {

        menuServiceCommand.delete(menuId);

        return ResponseEntity.noContent().build();
    }
}
