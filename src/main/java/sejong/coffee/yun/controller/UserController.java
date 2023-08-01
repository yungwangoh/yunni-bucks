package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.user.UserDto;
import sejong.coffee.yun.service.UserService;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Validated
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("")
    ResponseEntity<UserDto.Sign.Up.Response> signUp(@RequestBody @Valid UserDto.Sign.Up.Request request) {
        Member member = userService.signUp(
                request.name(),
                request.email(),
                request.password(),
                request.address()
        );

        UserDto.Sign.Up.Response response = modelMapper.map(member, UserDto.Sign.Up.Response.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-in")
    ResponseEntity<UserDto.Sign.In.Response> signIn(@RequestBody @Valid UserDto.Sign.In.Request request) {
        String accessToken = userService.signIn(request.email(), request.password());

        UserDto.Sign.In.Response response = modelMapper.map(accessToken, UserDto.Sign.In.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sign-out")
    ResponseEntity<Void> signOut(@RequestHeader(AUTHORIZATION) String accessToken, @MemberId Long memberId) {
        userService.signOut(accessToken, memberId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/order-list")
    ResponseEntity<UserDto.Order.Response> findOrders(@MemberId Long memberId) {
        List<Order> orders = userService.findAllByMemberId(memberId);

        UserDto.Order.Response response = modelMapper.map(orders, UserDto.Order.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    ResponseEntity<UserDto.Find.Response> findById(@MemberId Long memberId) {
        Member member = userService.findMember(memberId);

        UserDto.Find.Response response = modelMapper.map(member, UserDto.Find.Response.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("")
    ResponseEntity<Void> delete(@MemberId Long memberId) {
        userService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email")
    ResponseEntity<Void> updateEmail(@MemberId Long memberId, @RequestParam("email") String email) {
        userService.updateEmail(memberId, email);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/name")
    ResponseEntity<Void> updateName(@MemberId Long memberId, @RequestParam("name") String name) {
        userService.updateName(memberId, name);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    ResponseEntity<Void> updatePassword(@MemberId Long memberId, @RequestParam("password") String password) {
        userService.updatePassword(memberId, password);

        return ResponseEntity.ok().build();
    }
}
