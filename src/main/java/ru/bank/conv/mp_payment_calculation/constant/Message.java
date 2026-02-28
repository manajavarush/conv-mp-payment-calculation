package ru.bank.conv.mp_payment_calculation.constant;

import lombok.Getter;

@Getter
public enum Message {
    // ТЗ-1 ФТ_1
    OK("OK"),
    UIS_NOT_AVAILABLE("UIS is not available"),

    // ТЗ-1 ФТ_2
    CLIENT_ALREADY_EXISTS("Client already exists"),
    BAD_REQUEST("Bad Request"),

    // ТЗ-2 ФТ_1
    DB_IS_NOT_AVAILABLE("DB is not available"),

    // TЗ-2 ФТ_2
    PAYMENTS_NOT_FOUND("Not found payments"),

    // TZ-3
    DATA_NOT_FOUND("There is no data available. It is necessary to send a request to Inversion");

    private final String text;

    Message(String text) {
        this.text = text;
    }
}
