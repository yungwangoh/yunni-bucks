package sejong.coffee.yun.controller.advise;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejong.coffee.yun.domain.exception.*;
import sejong.coffee.yun.dto.error.ErrorResult;

import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<ErrorResult> notFoundException(NotFoundException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.NOT_FOUND, e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
    }

    @ExceptionHandler(NotMatchUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> userNotMatchException(NotMatchUserException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(MenuException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> menuException(MenuException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> illegalArgumentException(IllegalArgumentException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<ErrorResult> illegalStateException(IllegalStateException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.NOT_FOUND, e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<ErrorResult> runTimeException(RuntimeException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> duplicateException(DuplicatedException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(DuplicatedNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> duplicateNameException(DuplicatedNameException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> duplicateEmailException(DuplicatedEmailException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, INPUT_ERROR.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResult> jwtException(JwtException e) {

        ErrorResult errorResult = getErrorResult(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    private static ErrorResult getErrorResult(HttpStatus status, String e) {
        return new ErrorResult(status, e);
    }
}
