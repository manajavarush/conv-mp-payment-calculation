package ru.bank.conv.mp_payment_calculation.constant;

import lombok.Getter;

@Getter
public enum ExceptionMessages {
    OK("OK"),
    UIS_NOT_AVAILABLE("UIS is not available"),

    CLIENT_ALREADY_EXISTS("Client already exists"),
    BAD_REQUEST("Bad Request"),

    DB_IS_NOT_AVAILABLE("DB is not available"),

    PAYMENTS_NOT_FOUND("Not found payments"),

    DATA_NOT_FOUND("There is no data available. It is necessary to send a request to Inversion");

    private final String text;

    ExceptionMessages(String text) {
        this.text = text;
    }
}
