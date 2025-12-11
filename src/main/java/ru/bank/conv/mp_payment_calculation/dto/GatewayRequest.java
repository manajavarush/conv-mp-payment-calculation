package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос к внешнему сервису UIS")
public record GatewayRequest(@Schema(description = "ID интеграции", example = "2") Long integrationId,
                             @Schema(description = "Задание для обработки")
                             GatewayTask task,
                             @Schema(description = "Источник запроса", example = "conv-mp-payment-calculation")
                             String source) {
}
