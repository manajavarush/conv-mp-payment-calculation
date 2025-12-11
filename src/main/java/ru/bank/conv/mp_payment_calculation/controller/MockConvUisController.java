package ru.bank.conv.mp_payment_calculation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.conv.mp_payment_calculation.constant.Message;
import ru.bank.conv.mp_payment_calculation.dto.GatewayRequest;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.service.MockConvUisService;

/**
 * Эмуляция внешнего сервиса conv-uis-gateway.
 * Возвращает 4 раза 200 OK и каждый 5-й раз 500 UIS_NOT_AVAILABLE.
 */

@RestController
@RequestMapping("/v1/conv-uis-gateway")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Conv UIS Gateway Mock",
        description = "Мок внешнего сервиса UIS: принимает JSON-запросы и возвращает цикличные ответы (4×OK, 5-й — ошибка 500)"
)
public class MockConvUisController {

    private final MockConvUisService mockConvUisService;

    /**
     * Принимает JSON-запрос от основного сервиса и имитирует поведение внешнего UIS.
     */
    @Operation(
            summary = "Получить ответ от mock conv-uis-gateway",
            description = "ФТ_1 / ФТ_2: Принимает GatewayRequest и возвращает JSON с message = 'OK' (200)" +
                          " или 'UIS is not available' (500)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Выполнено успешно",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GatewayResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"OK\"}"))),
            @ApiResponse(responseCode = "500", description = "Ошибка на стороне UIS",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"UIS is not available\"}")
                    ))
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GatewayResponse> receive(@RequestBody GatewayRequest request) {

        long requestNumber = mockConvUisService.next();
        log.info("Получен запрос №{} в mock conv-uis-gateway: {}", requestNumber, request);

        if (mockConvUisService.isFailure(requestNumber)) {
            log.warn("UIS mock возвращает ошибку (500) — эмуляция недоступности UIS");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GatewayResponse(Message.UIS_NOT_AVAILABLE.getText()));
        }

        log.info("UIS mock возвращает успешный ответ (200)");
        return ResponseEntity.ok(new GatewayResponse(Message.OK.getText()));
    }
}
