package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

/**
 * Ошибка запроса(500) -> DB is not available
 */
public class DbUnavailableException extends RuntimeException {
    public DbUnavailableException() {
        super(Message.DB_IS_NOT_AVAILABLE.getText());
    }
}
