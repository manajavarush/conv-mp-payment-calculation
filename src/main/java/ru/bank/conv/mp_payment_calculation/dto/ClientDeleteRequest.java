package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ClientDeleteRequest(// @NotEmpty == НЕ null + empty == Optional.ofNullable(rawInns).orElseGet(List::of)
                                  @Schema(description = "Список ИНН", example = "[\"770708389300\", \"012345678901\"]")
                                  List<String> inns) {
}
