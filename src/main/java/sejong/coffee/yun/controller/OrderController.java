package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.dto.order.OrderDto;
import sejong.coffee.yun.dto.order.OrderPageDto;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.OrderService;

import java.time.LocalDateTime;

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

        Order order = orderService.order(memberId, cart.getMenuList(), LocalDateTime.now());

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cancel")
    ResponseEntity<Void> orderCancel(@RequestParam("orderId") Long orderId) {

        orderService.cancel(orderId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/add")
    ResponseEntity<OrderDto.Response> updateAddMenu(@MemberId Long memberId,
                                                    @RequestParam("menuId") Long menuId) {

        Order order = orderService.updateAddMenu(memberId, menuId, LocalDateTime.now());

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/remove")
    ResponseEntity<OrderDto.Response> updateRemoveMenu(@MemberId Long memberId,
                                                       @RequestParam("menuIdx") int menuIdx) {

        Order order = orderService.updateRemoveMenu(memberId, menuIdx, LocalDateTime.now());

        OrderDto.Response response = customMapper.map(order, OrderDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pageNum}")
    ResponseEntity<OrderPageDto.Response> findAllByMember(@MemberId Long memberId, @PathVariable int pageNum) {

        PageRequest pr = PageRequest.of(pageNum, 10);

        Page<Order> orderPage = orderService.findAllByMemberId(pr, memberId);

        OrderPageDto.Response response = customMapper.map(orderPage, OrderPageDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pageNum}/order-status")
    ResponseEntity<OrderPageDto.Response> findOrderStatusAllByMember(@MemberId Long memberId, @PathVariable int pageNum) {

        PageRequest pr = PageRequest.of(pageNum, 10);

        Page<Order> orderPage = orderService.findAllByMemberIdAndOrderStatus(pr, memberId);

        OrderPageDto.Response response = customMapper.map(orderPage, OrderPageDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pageNum}/paid-status")
    ResponseEntity<OrderPageDto.Response> findPayStatusAllByMember(@MemberId Long memberId, @PathVariable int pageNum) {

        PageRequest pr = PageRequest.of(pageNum, 10);

        Page<Order> orderPage = orderService.findAllByMemberIdAndPayStatus(pr, memberId);

        OrderPageDto.Response response = customMapper.map(orderPage, OrderPageDto.Response.class);

        return ResponseEntity.ok(response);
    }
 }
