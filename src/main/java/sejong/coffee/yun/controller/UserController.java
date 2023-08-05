package sejong.coffee.yun.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.user.UserDto;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final CustomMapper customMapper;

    @PostMapping("")
    ResponseEntity<UserDto.Sign.Up.Response> signUp(@RequestBody @Valid UserDto.Sign.Up.Request request) {

        log.info("request = {}", request.email());

        Member member = userService.signUp(
                request.name(),
                request.email(),
                request.password(),
                request.address()
        );

        UserDto.Sign.Up.Response response = customMapper.map(member, UserDto.Sign.Up.Response.class);


        log.info("response = {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-in")
    ResponseEntity<UserDto.Sign.In.Response> signIn(@RequestBody @Valid UserDto.Sign.In.Request request) {
        String accessToken = userService.signIn(request.email(), request.password());

        UserDto.Sign.In.Response response = customMapper.map(accessToken, UserDto.Sign.In.Response.class);

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

        UserDto.Order.Response response = customMapper.map(orders, UserDto.Order.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    ResponseEntity<UserDto.Find.Response> findById(@MemberId Long memberId) {
        Member member = userService.findMember(memberId);

        UserDto.Find.Response response = customMapper.map(member, UserDto.Find.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    ResponseEntity<List<UserDto.Find.Response>> findAll() {
        List<Member> members = userService.findAll();

        List<UserDto.Find.Response> collect = members.stream()
                .map(UserDto.Find.Response::new)
                .toList();

        return ResponseEntity.ok(collect);
    }

    @DeleteMapping("")
    ResponseEntity<Void> delete(@MemberId Long memberId) {
        userService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email")
    ResponseEntity<UserDto.Update.Response> updateEmail(@MemberId Long memberId,
                                                        @RequestBody @Valid UserDto.Update.Email.Request request) {

        Member member = userService.updateEmail(memberId, request.updateEmail());

        UserDto.Update.Response response = customMapper.map(member, UserDto.Update.Response.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/name")
    ResponseEntity<UserDto.Update.Response> updateName(@MemberId Long memberId,
                                                       @RequestBody @Valid UserDto.Update.Name.Request request) {

        Member member = userService.updateName(memberId, request.updateName());

        UserDto.Update.Response response = customMapper.map(member, UserDto.Update.Response.class);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/password")
    ResponseEntity<UserDto.Update.Response> updatePassword(@MemberId Long memberId,
                                                           @RequestBody @Valid UserDto.Update.Password.Request request) {

        Member member = userService.updatePassword(memberId, request.updatePassword());

        UserDto.Update.Response response = customMapper.map(member, UserDto.Update.Response.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/duplication/name")
    ResponseEntity<String> duplicateName(@RequestParam("name") String name) {
        String s = userService.duplicateName(name);

        return ResponseEntity.ok(s);
    }

    @GetMapping("/duplication/email")
    ResponseEntity<String> duplicateEmail(@RequestParam("email") String email) {
        String s = userService.duplicateEmail(email);

        return ResponseEntity.ok(s);
    }
}
