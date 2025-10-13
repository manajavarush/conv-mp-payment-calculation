package ru.bank.conv.mp_payment_calculation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Уточнить по названию массива id : clientId, clientID или client_id
// Важно, чтобы JSON-ключ, который ожидает внешний сервис, совпадал с тем, что сериализует Jackson.
public record GatewayTask(@JsonProperty("client_id")
                          List<Long> clientIds)
                          // @JsonFormat(shape = JsonFormat.Shape.ARRAY)
                          // Long[] clientId) {
{}
