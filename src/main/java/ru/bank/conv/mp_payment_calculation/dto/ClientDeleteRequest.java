package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Запрос на удаление клиентов")
public record ClientDeleteRequest(
        @Schema(description = "Список ИНН для удаления", example = "[\"770708389300\", \"012345678901\"]")
        List<String> inns) {
}
