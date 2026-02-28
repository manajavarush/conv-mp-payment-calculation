package ru.bank.conv.mp_payment_calculation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.bank.conv.mp_payment_calculation.constant.ExceptionMessages;

import java.util.List;

@Schema(description = "Ответ на удаление клиентов")
public record ClientDeleteResponse(@Schema(description = "Сообщение ответа", example = "OK")
                                   String message,
                                   @Schema(description = "Количество успешно удалённых клиентов", example = "2")
                                   int updatedCount,
                                   @Schema(description = "Список ИНН клиентов, которые не были найдены в системе",
                                           example = "[\"1234567890\", \"0987654321\"]")
                                   List<String> notFoundInns) {

    public static ClientDeleteResponse success(int deletedCount, List<String> notFounds) {
        return new ClientDeleteResponse(
                ExceptionMessages.OK.getText(),
                deletedCount,
                notFounds
        );
    }
}
