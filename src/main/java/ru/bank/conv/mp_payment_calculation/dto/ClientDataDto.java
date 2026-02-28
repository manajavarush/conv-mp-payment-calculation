package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Данные о клиенте и его платежах")
public record ClientDataDto(
        @Schema(description = "ID клиента в Инверсии", example = "1234567")
        Long id,

        @Schema(description = "ИНН клиента", example = "770708389300")
        String inn,

        @Schema(description = "Наименование клиента", example = "ООО Альфа")
        String name,

        @Schema(description = "Список платежей клиента")
        List<PaymentDto> payments) {
}
