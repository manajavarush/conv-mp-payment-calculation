package ru.bank.conv.mp_payment_calculation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.bank.conv.mp_payment_calculation.dto.*;
import ru.bank.conv.mp_payment_calculation.exception.ClientAlreadyExistException;
import ru.bank.conv.mp_payment_calculation.exception.DataNotFoundException;
import ru.bank.conv.mp_payment_calculation.exception.PaymentsNotFoundException;
import ru.bank.conv.mp_payment_calculation.exception.handler.GlobalExceptionHandler;
import ru.bank.conv.mp_payment_calculation.service.PaymentMonitoringService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentMonitoringControllerTest {

    @Mock
    private PaymentMonitoringService service;

    @InjectMocks
    private PaymentMonitoringController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("GET /client-payments/sync - должен вернуть 200 OK")
    void syncPayments_shouldReturnOk() throws Exception {
        when(service.syncActiveClientsPayments()).thenReturn(new GatewayResponse("OK"));

        mockMvc.perform(get("/payments-monitoring/client-payments/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("GET /clients/register - должен вернуть 200 OK при успешной регистрации")
    void registerClients_shouldReturnOk() throws Exception {
        when(service.registerClientsForMonitoring(anyList())).thenReturn(new GatewayResponse("OK"));

        mockMvc.perform(get("/payments-monitoring/clients/register")
                        .param("id", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("GET /clients/register - должен вернуть 400 если клиент уже существует")
    void registerClients_shouldReturn400_ifClientExists() throws Exception {
        when(service.registerClientsForMonitoring(anyList())).thenThrow(new ClientAlreadyExistException());

        mockMvc.perform(get("/payments-monitoring/clients/register")
                        .param("id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Client already exists"));
    }

    @Test
    @DisplayName("DELETE /clients - должен вернуть 200 и количество удаленных")
    void deleteClients_shouldReturnOk() throws Exception {
        ClientDeleteResponse response = new ClientDeleteResponse("OK", 1, List.of());
        when(service.softDeleteClients(anyList())).thenReturn(response);

        mockMvc.perform(delete("/payments-monitoring/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inns\": [\"1234567890\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCount").value(1));
    }

    @Test
    @DisplayName("GET /client-payments/{inn}/{date} - должен вернуть детали платежей")
    void getPaymentDetails_shouldReturnDetails() throws Exception {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 1, 10, 0);
        ClientPaymentsResponse response = new ClientPaymentsResponse(dt,
                new ClientDataDto(1L, "123", "Test", List.of())
        );

        when(service.getClientPaymentsDetails(any(String.class), any(LocalDateTime.class))).thenReturn(response);

        mockMvc.perform(get("/payments-monitoring/client-payments/123/dateTime/2024-01-01T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientData.inn").value("123"));
    }

    @Test
    @DisplayName("GET /client-payments/{inn}/{date} - должен вернуть 404 если платежей нет")
    void getPaymentDetails_shouldReturn404_ifNotFound() throws Exception {
        when(service.getClientPaymentsDetails(any(String.class), any(LocalDateTime.class)))
                .thenThrow(new PaymentsNotFoundException());

        mockMvc.perform(get("/payments-monitoring/client-payments/123/dateTime/2024-01-01T10:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found payments"));
    }

    @Test
    @DisplayName("GET /client-balances - должен вернуть 200 и балансы")
    void getBalances_shouldReturnOk() throws Exception {
        ClientBalancesResponse response = ClientBalancesResponse.empty();
        when(service.getActualClientBalances()).thenReturn(response);

        mockMvc.perform(get("/payments-monitoring/client-balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientData").isArray());
    }

    @Test
    @DisplayName("GET /client-balances - должен вернуть 404 если данных нет")
    void getBalances_shouldReturn404_ifNoData() throws Exception {
        when(service.getActualClientBalances()).thenThrow(new DataNotFoundException());

        mockMvc.perform(get("/payments-monitoring/client-balances"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("There is no data available. It is necessary to send a request to Inversion"));
    }
}
