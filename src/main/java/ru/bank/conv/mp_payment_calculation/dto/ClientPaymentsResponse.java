package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Ответ с детализацией платежей клиента")
public record ClientPaymentsResponse(
        @Schema(description = "Дата и время получения данных", example = "2024-01-01T09:00:00")
        LocalDateTime dateTime,

        @Schema(description = "Данные клиента и список платежей")
        ClientDataDto clientData) {
}
