package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

public class ClientAlreadyExistException extends RuntimeException {
    public ClientAlreadyExistException() {
        super(ExceptionMessages.CLIENT_ALREADY_EXISTS.getText());
    }
}
