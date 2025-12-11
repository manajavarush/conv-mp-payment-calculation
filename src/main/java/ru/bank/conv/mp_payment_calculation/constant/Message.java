package ru.bank.conv.mp_payment_calculation.constant;

import lombok.Getter;

@Getter
public enum Message {
    // ФТ_1
    OK("OK"),
    UIS_NOT_AVAILABLE("UIS is not available"),

    // ФТ_2
    CLIENT_ALREADY_EXISTS("Client already exists"),
    BAD_REQUEST("Bad Request");

    private final String text;

    Message(String text) {
        this.text = text;
    }
}
