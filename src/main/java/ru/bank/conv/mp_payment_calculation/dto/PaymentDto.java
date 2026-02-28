package ru.bank.conv.mp_payment_calculation.dto;

import java.math.BigDecimal;

public record PaymentDto(BigDecimal amount,
                         String paymentDirection,
                         String paymentTo,
                         String paymentFrom,
                         String outBank,
                         String status,
                         String description) {
}
