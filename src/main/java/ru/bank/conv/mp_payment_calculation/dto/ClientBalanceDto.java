package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Данные о балансе и платежах клиента для главного экрана")
public record ClientBalanceDto(
        @Schema(description = "ИНН клиента", example = "7736050003")
        String inn,

        @Schema(description = "Наименование клиента", example = "ООО Ромашка")
        String name,

        @Schema(description = "Входящий остаток (Morning Balance)", example = "100000.00")
        BigDecimal incomeBalance,

        @Schema(description = "Текущий остаток", example = "95000.50")
        BigDecimal currentBalance,

        @Schema(description = "Плановый остаток", example = "80000.00")
        BigDecimal planBalance,

        @Schema(description = "Внешние плановые списания", example = "5000.00")
        BigDecimal externalPlannedLeave,

        @Schema(description = "Внутренние плановые списания", example = "10000.00")
        BigDecimal internalPlannedLeave,

        @Schema(description = "Внешние фактические списания", example = "2000.00")
        BigDecimal externalFactLeave,

        @Schema(description = "Внутренние фактические списания", example = "3000.00")
        BigDecimal internalFactLeave,

        @Schema(description = "Внешние фактические поступления", example = "1500.00")
        BigDecimal externalFactIncome,

        @Schema(description = "Внутренние фактические поступления", example = "500.00")
        BigDecimal internalFactIncome
) {
}
