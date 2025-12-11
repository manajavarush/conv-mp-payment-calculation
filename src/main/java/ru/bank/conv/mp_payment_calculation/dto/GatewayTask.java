package ru.bank.conv.mp_payment_calculation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Задание для обработки клиентских ID")
public record GatewayTask(@JsonProperty("clientId")
                          @Schema(description = "ID клиентов для обработки", example = "[1, 2, 3]")
                          List<Long> clientIds) {
}
