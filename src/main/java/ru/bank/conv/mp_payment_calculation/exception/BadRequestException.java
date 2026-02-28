package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super(ExceptionMessages.BAD_REQUEST.getText());
    }
}
