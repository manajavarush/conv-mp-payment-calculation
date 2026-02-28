package ru.bank.conv.mp_payment_calculation.dto;

import java.time.LocalDateTime;

public record ClientPaymentsResponse(LocalDateTime dateTime,
                                     ClientDataDto clientData) {
}
