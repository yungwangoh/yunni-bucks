package sejong.coffee.yun.util.password;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void 비밀번호_암호화_테스트() {
        // given
        String pwd = "qwer1234@A";

        // when
        String s = PasswordUtil.encryptPassword(pwd);

        // then
        System.out.println("pwd : " + s);
    }
}