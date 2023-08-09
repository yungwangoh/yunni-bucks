package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.dto.order.OrderDto;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final CustomMapper customMapper;

    @PostMapping("")
    ResponseEntity<OrderDto.Response> order(@MemberId Long memberId) {

        Cart cart = cartService.findCartByMember(memberId);

        Order order = orderService.order(memberId, cart.getMenuList());

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    ResponseEntity<OrderDto.Response> findOrder(@MemberId Long memberId) {

        Order order = orderService.findOrderByMemberId(memberId);

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    ResponseEntity<Void> orderCancel(@RequestParam("orderId") Long orderId) {

        orderService.cancel(orderId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("")
    ResponseEntity<OrderDto.Response> updateAddMenu(@MemberId Long memberId,
                                                    @RequestParam("menuId") Long menuId) {

        Order order = orderService.updateAddMenu(memberId, menuId);

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("")
    ResponseEntity<OrderDto.Response> updateRemoveMenu(@MemberId Long memberId,
                                                       @RequestParam("menuIdx") int menuIdx) {

        Order order = orderService.updateRemoveMenu(memberId, menuIdx);

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.ok(response);
    }
 }
