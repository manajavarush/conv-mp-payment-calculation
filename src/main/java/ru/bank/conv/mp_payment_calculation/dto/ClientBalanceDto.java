package ru.bank.conv.mp_payment_calculation.dto;

import java.math.BigDecimal;

public record ClientBalanceDto(
        String inn,
        String name,
        BigDecimal incomeBalance,    // morning_balance
        BigDecimal currentBalance,
        BigDecimal planBalance, // calculated
        BigDecimal externalPlannedLeave,
        BigDecimal internalPlannedLeave,
        BigDecimal externalFactLeave,
        BigDecimal internalFactLeave,
        BigDecimal externalFactIncome,
        BigDecimal internalFactIncome
) {
}
