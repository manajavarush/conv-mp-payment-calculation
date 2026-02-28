package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Информация о платеже")
public record PaymentDto(
        @Schema(description = "Сумма платежа", example = "1256347.56")
        BigDecimal amount,

        @Schema(description = "Направление платежа", example = "Входящий", allowableValues = {"Входящий", "Исходящий", "Нет данных"})
        String paymentDirection,

        @Schema(description = "Получатель платежа", example = "Совкомбанк")
        String paymentTo,

        @Schema(description = "Отправитель платежа", example = "Сбербанк")
        String paymentFrom,

        @Schema(description = "Тип банка (Внешний/Внутренний)", example = "Внешний", allowableValues = {"Внешний", "Внутренний", "Нет данных"})
        String outBank,

        @Schema(description = "Статус платежа", example = "Исполнен", allowableValues = {"Исполнен", "Не исполнен", "Нет данных"})
        String status,

        @Schema(description = "Назначение платежа", example = "Оплата по договору №123")
        String description) {
}
