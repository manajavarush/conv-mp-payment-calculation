package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Ответ с актуальными балансами клиентов")
public record ClientBalancesResponse(
        @Schema(description = "Дата и время формирования данных", example = "2024-01-01T10:00:00")
        LocalDateTime datetime,

        @Schema(description = "Список данных по клиентам")
        List<ClientBalanceDto> clientData) {

    public static ClientBalancesResponse empty() {
        return new ClientBalancesResponse(LocalDateTime.now(), List.of());
    }
}
