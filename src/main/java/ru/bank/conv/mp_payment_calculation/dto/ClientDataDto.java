package ru.bank.conv.mp_payment_calculation.dto;

import java.util.List;

public record ClientDataDto(Long id,
                            String inn,
                            String name,
                            List<PaymentDto> payments) {
}
