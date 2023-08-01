package sejong.coffee.yun.dto.user;

import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.dto.order.OrderDto;
import sejong.coffee.yun.util.regex.RegexUtil;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
            ) {}
            public record Response(Long memberId, String name, String email, Address address, UserRank userRank, Money money) {}
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

    }

    public static class Delete {

    }

    public static class Find {
        public record Response(Long memberId, String name, String email, Address address, UserRank userRank, Money money) {}
    }

    public static class Order {
        public record Response(List<OrderDto.Order.Response> orders) {}
    }
}
