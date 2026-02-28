package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

public class DbUnavailableException extends RuntimeException {
    public DbUnavailableException() {
        super(ExceptionMessages.DB_IS_NOT_AVAILABLE.getText());
    }
}
