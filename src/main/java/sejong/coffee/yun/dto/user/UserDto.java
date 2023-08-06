package sejong.coffee.yun.dto.user;

import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.dto.order.OrderDto;
import sejong.coffee.yun.util.regex.RegexUtil;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

public class UserDto {
    public static class Sign {
        public static class Up {
            public record Request(
                    @Pattern(regexp = RegexUtil.NAME)
                    String name,
                    @Email
                    String email,
                    @Pattern(regexp = RegexUtil.PASSWORD)
                    String password,
                    @NotNull
                    Address address
            ){}
        }

        public static class In {
            public record Request(
                    @Email
                    String email,
                    @Pattern(regexp = RegexUtil.PASSWORD)
                    String password
            ) {}

            public record Response(String accessToken) {}
        }
    }

    public static class Update {
        public static class Name {
            public record Request(@Pattern(regexp = RegexUtil.NAME) String updateName) {}
        }

        public static class Password {
           public record Request(@Pattern(regexp = RegexUtil.PASSWORD) String updatePassword) {}
        }

        public static class Email {
            public record Request(@javax.validation.constraints.Email String updateEmail) {}
        }
    }

    public static class Order {
        public record Response(List<OrderDto.Response> orders) {}
    }

    public record Response(Long memberId, String name, String email,
                           Address address, UserRank userRank, Money money,
                           LocalDateTime createAt, LocalDateTime updateAt) {
        public Response(Member member) {
            this(
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getAddress(),
                    member.getUserRank(),
                    member.getMoney(),
                    member.getCreateAt(),
                    member.getUpdateAt()
            );
        }
    }
}
