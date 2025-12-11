package ru.bank.conv.mp_payment_calculation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.conv.mp_payment_calculation.constant.SwaggerMessages;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.service.PaymentMonitoringService;

import java.util.List;

@RestController
@RequestMapping("/payments-monitoring")
@RequiredArgsConstructor
@Validated
@Slf4j
// группировка эндпоинтов по группам
@Tag(name = "Payment Monitoring", description = "Мониторинг и обновление клиентских платежей")
public class PaymentMonitoringController {

    private final PaymentMonitoringService paymentMonitoringService;

    /**
     * ФТ_1 - обновление платежей всех активных клиентов.
     */

    @Operation(
            summary = "Запросить обновление платежей клиентов во внешнем UIS",
            description = "ФТ_1: Формирует JSON и вызывает conv-uis-gateway для синхронизации платежей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Выполнено успешно",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GatewayResponse.class),// Swagger покажет структуру тела ответа
                            examples = @ExampleObject(value = "{\"message\": \"OK\"}"))),
            @ApiResponse(responseCode = "500", description = "Ошибка на стороне UIS",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"UIS is not available\"}")
                    ))
    })
    @GetMapping("/client-payments/sync")
    public ResponseEntity<GatewayResponse> syncActiveClientsPayments() {
        log.info("Запрос на обновление платежей клиентов");
        GatewayResponse response = paymentMonitoringService.syncActiveClientsPayments();

        log.info("Ответ от conv-uis-gateway: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * ФТ_2 — добавление клиентов по ID.
     */
    @Operation(
            summary = "Добавить клиентов в систему мониторинга",
            description = "ФТ_2: Регистрирует клиентов в системе мониторинга и инициирует вызов внешнего UIS при необходимости"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = SwaggerMessages.OK,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GatewayResponse.class),// Swagger покажет структуру тела ответа
                            examples = @ExampleObject(value = SwaggerMessages.EXAMPLE_OK))),
            @ApiResponse(responseCode = "400", description = "Некорректный формат сообщения " +
                                                             "или Запрашиваемый клиент уже существует",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "invalid_format", value = SwaggerMessages.EXAMPLE_BAD_REQUEST),
                                    @ExampleObject(name = "duplicate_client", value = SwaggerMessages.EXAMPLE_CLIENT_EXISTS)
                            }
                    )),
            @ApiResponse(responseCode = "500", description = "Ошибка на стороне UIS",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"UIS is not available\"}")
                    ))
    })
    @GetMapping("/clients/register")
    public ResponseEntity<GatewayResponse> registerClientsForMonitoring(
            @Parameter(description = "ID клиентов для добавления", example = "1, 2, 3")
            @RequestParam("id") @NotEmpty List<Long> clientIds) {
        log.info("Запрос на добавление клиентов: {}", clientIds);
        GatewayResponse response = paymentMonitoringService.registerClientsForMonitoring(clientIds);

        log.info("Ответ от conv-uis-gateway: {}", response);
        return ResponseEntity.ok(response);
    }
}
