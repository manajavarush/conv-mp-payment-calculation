package ru.bank.conv.mp_payment_calculation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ClientBalancesResponse(LocalDateTime datetime,
                                     List<ClientBalanceDto> clientData) {

    // Статический фабричный метод - возвращаем пустой массив и текущее время запроса
    public static ClientBalancesResponse empty() {
        return new ClientBalancesResponse(LocalDateTime.now(), List.of());
    }
}
