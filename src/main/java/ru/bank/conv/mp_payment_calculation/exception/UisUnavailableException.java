package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

public class UisUnavailableException extends RuntimeException {
    public UisUnavailableException() {
        super(ExceptionMessages.UIS_NOT_AVAILABLE.getText());
    }
}
