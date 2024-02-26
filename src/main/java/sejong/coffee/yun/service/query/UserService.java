package sejong.coffee.yun.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.repository.redis.NoSqlRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.time.Duration;
import java.util.List;

import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_EMAIL;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final NoSqlRepository noSqlRepository;

    public Member findMember(Long memberId) {
        return userRepository.findById(memberId);
    }

    public List<Member> findAll() {
        return userRepository.findAll();
    }

    public String duplicateName(String name) {
        userRepository.duplicateName(name);

        return SUCCESS_DUPLICATE_NAME.getMessage();
    }

    public String duplicateEmail(String email) {
        userRepository.duplicateEmail(email);

        return SUCCESS_DUPLICATE_EMAIL.getMessage();
    }

    private String userCheck(String password, Member member) {
        String accessToken;

        member.checkPasswordMatch(password);

        accessToken = jwtProvider.createAccessToken(member);
        String refreshToken = jwtProvider.createRefreshToken(member);

        noSqlRepository.setValues(String.valueOf(member.getId()), refreshToken, Duration.ofMillis(jwtProvider.fetchRefreshTokenExpireTime()));

        return accessToken;
    }
}
