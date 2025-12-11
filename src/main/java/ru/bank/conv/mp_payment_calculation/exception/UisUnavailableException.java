package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

/**
 * Ошибка запроса(500) -> UIS is not available
 */
public class UisUnavailableException extends RuntimeException {
    public UisUnavailableException() {
        super(Message.UIS_NOT_AVAILABLE.getText());
    }
}
