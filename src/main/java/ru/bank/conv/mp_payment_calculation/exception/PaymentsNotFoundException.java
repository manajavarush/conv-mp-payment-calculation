package ru.bank.conv.mp_payment_calculation.exception;

import ru.bank.conv.mp_payment_calculation.constant.Message;

/**
 * Ошибка запроса(404) -> Not found payments
 */
public class PaymentsNotFoundException extends RuntimeException {
    public PaymentsNotFoundException() {super(Message.PAYMENTS_NOT_FOUND.getText());}
}
