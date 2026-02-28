package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

/**
 * Ошибка запроса(404) -> Not found data
 */
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(){super(Message.DATA_NOT_FOUND.getText());}
}
