package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ от внешнего сервиса UIS")
public record GatewayResponse(@Schema(description = "Сообщение ответа", example = "OK")
                              String message) {
}
