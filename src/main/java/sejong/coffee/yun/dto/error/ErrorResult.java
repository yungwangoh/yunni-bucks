package sejong.coffee.yun.dto.error;

import org.springframework.http.HttpStatus;

public record ErrorResult(HttpStatus status, String message) {
}
