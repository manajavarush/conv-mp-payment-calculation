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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bank.conv.mp_payment_calculation.dto.*;
import ru.bank.conv.mp_payment_calculation.service.PaymentMonitoringService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.MediaType.*;
import static ru.bank.conv.mp_payment_calculation.constant.SwaggerMessages.*;

@RestController
@RequestMapping("/payments-monitoring")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Payment Monitoring", description = "Мониторинг и обновление клиентских платежей")
public class PaymentMonitoringController {

    private final PaymentMonitoringService paymentMonitoringService;

    /**
     * ТЗ-1 ФТ_1: Синхронизация платежей активных клиентов через UIS.
     */

    @Operation(
            summary = "Запросить обновление платежей клиентов во внешнем UIS",
            description = "ФТ_1: Формирует JSON и вызывает conv-uis-gateway для синхронизации платежей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GatewayResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_OK))),
            @ApiResponse(responseCode = "500", description = UIS_ERROR,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_UIS_ERROR)
                    ))
    })
    @GetMapping("/client-payments/sync")
    public ResponseEntity<GatewayResponse> syncActiveClientsPayments() {
        log.info("Запрос на синхронизацию платежей клиентов");
        GatewayResponse response = paymentMonitoringService.syncActiveClientsPayments();
        log.info("Ответ от conv-uis-gateway: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * ТЗ-1 ФТ_2: Регистрация новых клиентов в системе.
     */
    @Operation(
            summary = "Добавить клиентов в систему мониторинга",
            description = "ФТ_2: Регистрирует клиентов в системе мониторинга и инициирует вызов внешнего UIS при необходимости"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GatewayResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_OK))),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST + " или " + CLIENT_EXISTS,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "invalid_format", value = EXAMPLE_BAD_REQUEST),
                                    @ExampleObject(name = "duplicate_client", value = EXAMPLE_CLIENT_EXISTS)
                            }
                    )),
            @ApiResponse(responseCode = "500", description = UIS_ERROR,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_UIS_ERROR)
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

    /**
     * ТЗ-2 ФТ_1: Мягкое удаление клиентов по ИНН.
     */
    @Operation(
            summary = "Удалить клиентов из системы (мягкое удаление)",
            description = "ТЗ-2 ФТ_1: Помечает клиентов как удаленных (is_deleted = true) по списку ИНН"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientDeleteResponse.class))),
            @ApiResponse(responseCode = "500", description = DB_ERROR,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_DB_ERROR)))
    })
    @DeleteMapping("/clients")
    public ResponseEntity<ClientDeleteResponse> softDeleteClients(@RequestBody ClientDeleteRequest request) {
        var response = paymentMonitoringService.softDeleteClients(request.inns());

        return ResponseEntity.ok(response);
    }

    /**
     * ТЗ-2 ФТ_2: Детализация платежей по клиенту.
     */
    @Operation(
            summary = "Детализация платежей по клиенту",
            description = "ТЗ-2 ФТ_2: Возвращает детализацию платежей клиента на указанную дату и время. " +
                          "Формат даты ISO 8601 (yyyy-MM-ddTHH:mm:ss)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientPaymentsResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_CLIENT_PAYMENTS))),

            @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_BAD_REQUEST))),

            @ApiResponse(responseCode = "404", description = PAYMENTS_NOT_FOUND,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_PAYMENTS_NOT_FOUND))),

            @ApiResponse(responseCode = "500", description = DB_ERROR,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_DB_ERROR)))
    })
    @GetMapping("/client-payments/{clientInn}/dateTime/{dateTime}")
    public ResponseEntity<ClientPaymentsResponse> getPaymentDetails(
            @Parameter(description = "ИНН клиента (10 или 12 цифр)", example = "012345678901")
            @PathVariable String clientInn,

            @Parameter(description = "Дата и время среза данных (ISO 8601)", example = "2024-01-10T10:30:00")
            @PathVariable LocalDateTime dateTime) {

        log.info("Request details: INN={}, Date={}", clientInn, dateTime);
        return ResponseEntity.ok(paymentMonitoringService.getClientPaymentsDetails(clientInn, dateTime));
    }

    /**
     * ТЗ-3: Получить актуальные данные по клиентам.
     */
    @Operation(
            summary = "Получить актуальные балансы клиентов",
            description = "ТЗ-3 ФТ_1: Возвращает данные по балансам и агрегированные платежи " +
                          "для главного экрана. Выбирает последний пакет данных для каждого клиента."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientBalancesResponse.class),
                            examples = @ExampleObject(value = EXAMPLE_CLIENT_BALANCES))),

            @ApiResponse(responseCode = "404", description = DATA_NOT_FOUND,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_DATA_NOT_FOUND))),

            @ApiResponse(responseCode = "500", description = DB_ERROR,
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = EXAMPLE_DB_ERROR)))
    })
    @GetMapping("/client-balances")
    public ResponseEntity<ClientBalancesResponse> getActualClientBalances() {
        log.info("Запрос актуальных балансов клиентов");
        return ResponseEntity.ok(paymentMonitoringService.getActualClientBalances());
    }
}
