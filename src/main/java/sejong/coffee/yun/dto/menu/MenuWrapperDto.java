package sejong.coffee.yun.dto.menu;

import java.util.List;

public class MenuWrapperDto {

    public record Response(List<MenuDto.Response> responses) {}
}
