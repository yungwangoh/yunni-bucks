package sejong.coffee.yun.service;

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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisRepository redisRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Member signUp(String name, String email, String password, Address address) {
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
    public void updateName(Long memberId, String updateName) {
        Member member = userRepository.findById(memberId);

        member.updateName(updateName);
    }

    @Transactional
    public void updateEmail(Long memberId, String updateEmail) {
        Member member = userRepository.findById(memberId);

        member.updateEmail(updateEmail);
    }

    @Transactional
    public void updatePassword(Long memberId, String updatePassword) {
        Member member = userRepository.findById(memberId);

        member.updatePassword(updatePassword);
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

        String accessToken = "";

        if(PasswordUtil.match(member.getPassword(), password)) {

            accessToken = jwtProvider.createAccessToken(member);
            String refreshToken = jwtProvider.createRefreshToken(member);

            redisRepository.setValues(email, refreshToken, Duration.ofMillis(jwtProvider.fetchRefreshTokenExpireTime()));
        } else {
            throw NOT_MATCH_USER.notMatchUserException();
        }

        return accessToken;
    }

    @Transactional
    public void signOut(String email) {
        redisRepository.deleteValues(email);
    }

    public List<Order> findAllByMemberId(Long memberId) {
        return orderRepository.findAllByMemberId(memberId);
    }
}
