package sejong.coffee.yun.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.redis.RedisRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.util.password.PasswordUtil;

import java.time.Duration;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_MATCH_USER;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_EMAIL;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_NAME;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisRepository redisRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Member signUp(String name, String email, String password, Address address) {

        userRepository.duplicateEmail(email);
        userRepository.duplicateName(name);

        Member member = Member.builder()
                .name(name)
                .email(email)
                .password(PasswordUtil.encryptPassword(password))
                .address(address)
                .money(Money.ZERO)
                .userRank(UserRank.BRONZE)
                .build();

        return userRepository.save(member);
    }

    public Member findMember(Long memberId) {
        return userRepository.findById(memberId);
    }

    @Transactional
    public Member updateName(Long memberId, String updateName) {

        userRepository.duplicateName(updateName);

        Member member = userRepository.findById(memberId);

        member.updateName(updateName);

        return member;
    }

    @Transactional
    public Member updateEmail(Long memberId, String updateEmail) {

        userRepository.duplicateEmail(updateEmail);

        Member member = userRepository.findById(memberId);

        member.updateEmail(updateEmail);

        return member;
    }

    @Transactional
    public Member updatePassword(Long memberId, String updatePassword) {
        Member member = userRepository.findById(memberId);

        member.updatePassword(updatePassword);

        return member;
    }

    public List<Member> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        userRepository.delete(memberId);
    }

    @Transactional
    public String signIn(String email, String password) {
        Member member = userRepository.findByEmail(email);

        String accessToken;

        if(PasswordUtil.match(member.getPassword(), password)) {

            accessToken = jwtProvider.createAccessToken(member);
            String refreshToken = jwtProvider.createRefreshToken(member);

            redisRepository.setValues(String.valueOf(member.getId()), refreshToken, Duration.ofMillis(jwtProvider.fetchRefreshTokenExpireTime()));
        } else {
            throw NOT_MATCH_USER.notMatchUserException();
        }

        return accessToken;
    }

    @Transactional
    public void signOut(String accessToken, Long memberId) {

        if(!jwtProvider.tokenExpiredCheck(accessToken))
            throw new JwtException("토큰이 만료되었습니다.");

        redisRepository.deleteValues(String.valueOf(memberId));

        Long tokenExpireTime = jwtProvider.getTokenExpireTime(accessToken);

        redisRepository.setValues(accessToken, "blackList", Duration.ofMillis(tokenExpireTime));
    }

    public List<Order> findAllByMemberId(Long memberId) {
        return orderRepository.findAllByMemberId(memberId);
    }

    public String duplicateName(String name) {
        userRepository.duplicateName(name);

        return SUCCESS_DUPLICATE_NAME.getMessage();
    }

    public String duplicateEmail(String email) {
        userRepository.duplicateEmail(email);

        return SUCCESS_DUPLICATE_EMAIL.getMessage();
    }
}
