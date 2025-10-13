package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

/** Ошибка: UIS недоступен (500). */
public class UisUnavailableException extends RuntimeException {
    public UisUnavailableException() { super(Message.UIS_NOT_AVAILABLE.getText()); }
}
