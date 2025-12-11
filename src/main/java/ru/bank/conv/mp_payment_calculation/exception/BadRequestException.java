package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

/**
 * Ошибка запроса (400) -> Bad Request
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super(Message.BAD_REQUEST.getText());
    }
}
