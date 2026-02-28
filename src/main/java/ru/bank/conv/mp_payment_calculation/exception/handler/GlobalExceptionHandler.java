package ru.bank.conv.mp_payment_calculation.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Вынести в enum (все сообщения строго по ТЗ) или оставить здесь ?
    private static final String INVALID_PARAM_FORMAT_MSG = "Invalid parameter format";

    /*
        По хорошему разнести ошибки валидации / бизнес-логики / внешнего UIS / БД
     */

    // Обработка ошибок доступности (UIS, DB)
    @ExceptionHandler({
            UisUnavailableException.class,
            DbUnavailableException.class
    })
    public ResponseEntity<GatewayResponse> handleUnavailableException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GatewayResponse(exception.getMessage()));
    }


    // Ошибка "Не найдено" (404)
    @ExceptionHandler({
            PaymentsNotFoundException.class,
            DataNotFoundException.class
    })
    public ResponseEntity<GatewayResponse> handleNotFoundException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new GatewayResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            ClientAlreadyExistException.class,
            BadRequestException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<GatewayResponse> handleBadRequestExceptions(Exception exception) {
        String message;

        if (exception instanceof MethodArgumentTypeMismatchException) {
            message = INVALID_PARAM_FORMAT_MSG;
        } else {
            message = exception.getMessage();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GatewayResponse(message));
    }
}
