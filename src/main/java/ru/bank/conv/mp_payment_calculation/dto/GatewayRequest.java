package ru.bank.conv.mp_payment_calculation.dto;

public record GatewayRequest(Long integrationId, GatewayTask task, String source) {
}
