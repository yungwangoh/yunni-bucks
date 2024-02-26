package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.repository.redis.NoSqlRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.util.jwt.JwtUtil;
import sejong.coffee.yun.util.password.PasswordUtil;

import java.time.Duration;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_MATCH_USER;
import static sejong.coffee.yun.domain.exception.ExceptionControl.TOKEN_EXPIRED;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_SIGN_OUT;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceCommand {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final NoSqlRepository noSqlRepository;

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
                .orderCount(0)
                .build();

        return userRepository.save(member);
    }

    public Member updateName(Long memberId, String updateName) {

        userRepository.duplicateName(updateName);

        Member member = userRepository.findById(memberId);

        member.updateName(updateName);

        return member;
    }

    public Member updateEmail(Long memberId, String updateEmail) {

        userRepository.duplicateEmail(updateEmail);

        Member member = userRepository.findById(memberId);

        member.updateEmail(updateEmail);

        return member;
    }

    public Member updatePassword(Long memberId, String updatePassword) {
        Member member = userRepository.findById(memberId);

        member.updatePassword(updatePassword);

        return member;
    }

    public void deleteMember(Long memberId) {
        userRepository.delete(memberId);
    }

    public String signIn(String email, String password) {

        String accessToken;

        try {
            Member member = userRepository.findByEmail(email);

            upgradeUserRank(member);

            accessToken = userCheck(password, member);

        } catch (Exception e) {
            log.error("login error = {} {}", e.getCause(), e.getMessage());
            throw NOT_MATCH_USER.notMatchUserException();
        }

        return accessToken;
    }

    private void upgradeUserRank(Member member) {
        member.upgradeUserRank(member.getOrderCount());
    }

    public String signOut(String accessToken, Long memberId) {

        String token = JwtUtil.getFormatToken(accessToken);

        if(jwtProvider.tokenExpiredCheck(token)) {

            noSqlRepository.deleteValues(String.valueOf(memberId));

            Long tokenExpireTime = jwtProvider.getTokenExpireTime(token);

            noSqlRepository.setValues(token, "blackList", Duration.ofMillis(tokenExpireTime));

            return SUCCESS_SIGN_OUT.getMessage();
        } else {
            throw TOKEN_EXPIRED.tokenExpiredException();
        }
    }

    private String userCheck(String password, Member member) {
        String accessToken;

        member.checkPasswordMatch(password);

        accessToken = jwtProvider.createAccessToken(member);
        String refreshToken = jwtProvider.createRefreshToken(member);

        noSqlRepository.setValues(String.valueOf(member.getId()), refreshToken, Duration.ofMillis(jwtProvider.fetchRefreshTokenExpireTime()));

        return accessToken;
    }

    public List<Member> updateAllUserRank() {

        List<Member> members = userRepository.findAll();

        members.forEach(this::upgradeUserRank);

        return members;
    }
}
