package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

public class PaymentsNotFoundException extends RuntimeException {
    public PaymentsNotFoundException() {super(ExceptionMessages.PAYMENTS_NOT_FOUND.getText());}
}
