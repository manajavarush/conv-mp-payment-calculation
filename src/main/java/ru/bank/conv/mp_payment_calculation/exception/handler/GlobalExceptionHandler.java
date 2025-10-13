package ru.bank.conv.mp_payment_calculation.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.exception.BadRequestException;
import ru.bank.conv.mp_payment_calculation.exception.ClientAlreadyExistException;
import ru.bank.conv.mp_payment_calculation.exception.UisUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UisUnavailableException.class)
    public ResponseEntity<GatewayResponse> handleUisUnavailable(UisUnavailableException exception) {
        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GatewayResponse(exception.getMessage()));
    }

    @ExceptionHandler(ClientAlreadyExistException.class)
    public ResponseEntity<GatewayResponse> handleClientAlreadyExist(ClientAlreadyExistException exception) {
        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST)
                .body(new GatewayResponse(exception.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GatewayResponse> handleBadRequest(BadRequestException exception) {
        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST)
                .body(new GatewayResponse(exception.getMessage()));
    }
}
