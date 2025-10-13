package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

public class ClientAlreadyExistException extends RuntimeException {
    public ClientAlreadyExistException() {
        super(Message.CLIENT_ALREADY_EXISTS.getText());
    }
}
