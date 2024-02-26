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
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.dto.delivery.DeliveryDto;
import sejong.coffee.yun.dto.delivery.DeliveryPageDto;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.DeliveryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final CustomMapper customMapper;

    @PostMapping("/reserve")
    ResponseEntity<DeliveryDto.Response> reserveDelivery(@RequestBody @Valid DeliveryDto.ReserveRequest request) {

        Delivery save = deliveryService.save(
                request.orderId(),
                request.address(),
                request.now(),
                request.reserveDate(),
                request.type()
        );

        DeliveryDto.Response response = customMapper.map(save, DeliveryDto.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("")
    ResponseEntity<DeliveryDto.Response> normalDelivery(@RequestBody @Valid DeliveryDto.NormalRequest request) {

        Delivery delivery = deliveryService.save(
                request.orderId(),
                request.address(),
                request.now(),
                request.type()
        );

        DeliveryDto.Response response = customMapper.map(delivery, DeliveryDto.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/address")
    ResponseEntity<DeliveryDto.Response> updateAddress(@RequestBody @Valid DeliveryDto.UpdateAddressRequest request) {

        Delivery updateAddress = deliveryService
                .updateAddress(request.deliveryId(), request.address(), request.now());

        DeliveryDto.Response response = customMapper.map(updateAddress, DeliveryDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deliveryId}")
    ResponseEntity<DeliveryDto.Response> delivery(@PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.normalDelivery(deliveryId);

        DeliveryDto.Response response = customMapper.map(delivery, DeliveryDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deliveryId}/cancel")
    ResponseEntity<DeliveryDto.Response> cancel(@PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.cancel(deliveryId);

        DeliveryDto.Response response = customMapper.map(delivery, DeliveryDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deliveryId}/complete")
    ResponseEntity<DeliveryDto.Response> complete(@PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.complete(deliveryId);

        DeliveryDto.Response response = customMapper.map(delivery, DeliveryDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/page/{pageNum}")
    ResponseEntity<DeliveryPageDto.Response> findAllByMemberId(@PathVariable int pageNum, @MemberId Long memberId) {
        PageRequest pr = PageRequest.of(pageNum, 10);
        Page<Delivery> deliveryPage = deliveryService.findAllByMemberId(pr, memberId);

        DeliveryPageDto.Response response = customMapper.map(deliveryPage, DeliveryPageDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/page/{pageNum}/delivery-type")
    ResponseEntity<DeliveryPageDto.Response> findDeliveryTypeAllByMemberId(@PathVariable int pageNum,
                                                                           @MemberId Long memberId,
                                                                           @RequestParam("type") DeliveryType type) {
       PageRequest pr = PageRequest.of(pageNum, 10);
       Page<Delivery> deliveryPage = deliveryService.findDeliveryTypeAllByMemberId(pr, memberId, type);

        DeliveryPageDto.Response response = customMapper.map(deliveryPage, DeliveryPageDto.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/page/{pageNum}/delivery-status")
    ResponseEntity<DeliveryPageDto.Response> findDeliveryStatusAllByMemberId(@PathVariable int pageNum,
                                                                             @MemberId Long memberId,
                                                                             @RequestParam("status") DeliveryStatus status) {
        PageRequest pr = PageRequest.of(pageNum, 10);
        Page<Delivery> deliveryPage = deliveryService.findDeliveryStatusAllByMemberId(pr, memberId, status);

        DeliveryPageDto.Response response = customMapper.map(deliveryPage, DeliveryPageDto.Response.class);

        return ResponseEntity.ok(response);
    }
}
