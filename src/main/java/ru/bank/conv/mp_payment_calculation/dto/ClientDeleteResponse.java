package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.bank.conv.mp_payment_calculation.constant.Message;

import java.util.List;

public record ClientDeleteResponse(@Schema(description = "Сообщение ответа", example = "OK")
                                   String message,
                                   @Schema(description = "Количество успешно удалённых клиентов", example = "2")
                                   int updatedCount,
                                   @Schema(description = "Список ИНН клиентов, которые не были найдены в системе",
                                           example = "[\"1234567890\", \"0987654321\"]")
                                   List<String> notFoundInns) {

    // Интересная штука -> Static Factory Method
    public static ClientDeleteResponse success(int deletedCount, List<String> notFounds) {
        return new ClientDeleteResponse(
                Message.OK.getText(),
                deletedCount,
                notFounds
        );
    }
}
