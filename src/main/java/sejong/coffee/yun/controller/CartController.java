package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.dto.cart.CartDto;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.command.CartServiceCommand;
import sejong.coffee.yun.service.query.CartServiceQuery;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
@Validated
public class CartController {

    private final CartServiceCommand cartServiceCommand;
    private final CartServiceQuery cartServiceQuery;
    private final CustomMapper customMapper;

    @PostMapping("")
    ResponseEntity<CartDto.Response> cartCreate(@MemberId Long memberId) {

        Cart cart = cartServiceCommand.createCart(memberId);

        CartDto.Response response = customMapper.map(cart, CartDto.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("")
    ResponseEntity<Void> removeCart(@MemberId Long memberId) {

        cartServiceCommand.removeCart(memberId);


        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    ResponseEntity<CartDto.Response> getCart(@MemberId Long memberId) {

        Cart cart = cartServiceQuery.findCartByMember(memberId);

        CartDto.Response response = customMapper.map(cart, CartDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/menu")
    ResponseEntity<MenuDto.Response> getMenu(@MemberId Long memberId,
                                             @RequestParam("menuIdx") int menuIdx) {

        Menu menu = cartServiceQuery.getMenu(memberId, menuIdx);

        MenuDto.Response response = new MenuDto.Response(menu);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/menu")
    ResponseEntity<CartDto.Response> addMenu(@MemberId Long memberId,
                                       @RequestParam("menuId") Long menuId) {

        Cart cart = cartServiceCommand.addMenu(memberId, menuId);

        CartDto.Response response = customMapper.map(cart, CartDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/menu")
    ResponseEntity<CartDto.Response> removeMenu(@MemberId Long memberId,
                                                @RequestParam("menuIdx") int menuIdx) {

        Cart cart = cartServiceCommand.removeMenu(memberId, menuIdx);

        CartDto.Response response = customMapper.map(cart, CartDto.Response.class);

        return ResponseEntity.ok(response);
    }
}
