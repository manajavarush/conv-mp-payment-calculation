package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(){super(ExceptionMessages.DATA_NOT_FOUND.getText());}
}
