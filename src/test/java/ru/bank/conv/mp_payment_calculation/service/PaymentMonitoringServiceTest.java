package ru.bank.conv.mp_payment_calculation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.bank.conv.mp_payment_calculation.client.ConvUisGatewayClient;
import ru.bank.conv.mp_payment_calculation.config.GatewayProperties;
import ru.bank.conv.mp_payment_calculation.dto.*;
import ru.bank.conv.mp_payment_calculation.entity.Balance;
import ru.bank.conv.mp_payment_calculation.entity.Client;
import ru.bank.conv.mp_payment_calculation.entity.Payment;
import ru.bank.conv.mp_payment_calculation.exception.ClientAlreadyExistException;
import ru.bank.conv.mp_payment_calculation.exception.DataNotFoundException;
import ru.bank.conv.mp_payment_calculation.exception.PaymentsNotFoundException;
import ru.bank.conv.mp_payment_calculation.mapper.PaymentMapper;
import ru.bank.conv.mp_payment_calculation.repository.BalanceRepository;
import ru.bank.conv.mp_payment_calculation.repository.ClientRepository;
import ru.bank.conv.mp_payment_calculation.repository.PaymentRepository;
import ru.bank.conv.mp_payment_calculation.util.InnNormalizer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMonitoringServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private ConvUisGatewayClient gatewayClient;
    @Mock
    private GatewayProperties properties;
    @Mock
    private InnNormalizer innNormalizer;
    @Mock
    private PaymentAggregator paymentAggregator;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentMonitoringService service;

    @Test
    @DisplayName("Sync Payments: должен успешно отправить запрос в шлюз для активных клиентов")
    void syncActiveClientsPayments_shouldSendRequestToGateway() {
        when(clientRepository.findActiveClientIds()).thenReturn(List.of(1L, 2L));
        when(properties.getIntegrationId()).thenReturn(2L);
        when(properties.getSource()).thenReturn("source");
        when(gatewayClient.sendRequest(any())).thenReturn(
                new ResponseEntity<>(new GatewayResponse("OK"),
                HttpStatus.OK));

        GatewayResponse response = service.syncActiveClientsPayments();

        assertThat(response.message()).isEqualTo("OK");
        ArgumentCaptor<GatewayRequest> captor = ArgumentCaptor.forClass(GatewayRequest.class);
        verify(gatewayClient).sendRequest(captor.capture());
        assertThat(captor.getValue().task().clientIds()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("Register Clients: должен выбросить исключение, если клиент уже существует")
    void registerClientsForMonitoring_shouldThrowExceptionIfClientExists() {
        List<Long> ids = List.of(1L);
        when(clientRepository.findActiveClientIdsByIdIn(ids)).thenReturn(List.of(1L));

        assertThrows(ClientAlreadyExistException.class, () -> service.registerClientsForMonitoring(ids));
        verify(gatewayClient, never()).sendRequest(any());
    }

    @Test
    @DisplayName("Register Clients: должен успешно зарегистрировать новых клиентов")
    void registerClientsForMonitoring_shouldRegisterSuccessfully() {
        List<Long> ids = List.of(1L);

        when(clientRepository.findActiveClientIdsByIdIn(ids)).thenReturn(List.of());

        when(properties.getIntegrationId()).thenReturn(2L);
        when(properties.getSource()).thenReturn("source");
        when(gatewayClient.sendRequest(any())).thenReturn(
                new ResponseEntity<>(new GatewayResponse("OK"),
                HttpStatus.OK));

        GatewayResponse response = service.registerClientsForMonitoring(ids);

        assertThat(response.message()).isEqualTo("OK");

        verify(gatewayClient).sendRequest(any());
    }


    @Test
    @DisplayName("Get Payment Details: должен выбросить 404, если клиент не найден/не активен")
    void getClientPaymentsDetails_shouldThrowNotFoundIfClientNotActive() {
        String inn = "123";
        LocalDateTime dt = LocalDateTime.now();

        when(innNormalizer.normalizeSingle(inn)).thenReturn(Optional.of(inn));
        when(clientRepository.findByInnAndDeletedFalse(inn)).thenReturn(Optional.empty());

        assertThrows(PaymentsNotFoundException.class, () -> service.getClientPaymentsDetails(inn, dt));
    }


    @Test
    @DisplayName("Get Payment Details: должен возвращать детали платежей")
    void getClientPaymentsDetails_shouldReturnDetails() {
        String inn = "123";
        LocalDateTime dt = LocalDateTime.now();

        Client client = new Client(1L, inn, "Test", false, null);
        Payment payment = new Payment(); // Создаем пустой платеж для теста
        payment.setId(100L);
        payment.setClient(client);
        payment.setDateTime(dt);

        when(innNormalizer.normalizeSingle(inn)).thenReturn(Optional.of(inn));
        when(clientRepository.findByInnAndDeletedFalse(inn)).thenReturn(Optional.of(client));
        when(paymentRepository.findByClientIdAndPaymentDateTime(1L, dt)).thenReturn(List.of(payment));

        PaymentDto dto = new PaymentDto(BigDecimal.TEN, "In", "To", "From",
                "Ext", "Exec", "Desc");
        when(paymentMapper.mapToDtoList(List.of(payment))).thenReturn(List.of(dto));

        var response = service.getClientPaymentsDetails(inn, dt);

        assertThat(response).isNotNull();
        assertThat(response.clientData().inn()).isEqualTo(inn);
    }

    @Test
    @DisplayName("Delete Clients: должен успешно удалять клиентов и возвращать результат")
    void softDeleteClients_shouldDeleteSuccessfully() {
        List<String> rawInns = List.of("123");

        when(innNormalizer.normalize(rawInns)).thenReturn(rawInns);

        Client client = new Client(1L, "123", "Test", false, null);
        when(clientRepository.findByInnIn(rawInns)).thenReturn(List.of(client));

        when(clientRepository.softDeleteActiveByInnIn(rawInns)).thenReturn(1);

        ClientDeleteResponse response = service.softDeleteClients(rawInns);

        assertThat(response.updatedCount()).isEqualTo(1);
        assertThat(response.notFoundInns()).isEmpty();
        assertThat(response.message()).isEqualTo("OK");
    }

    @Test
    @DisplayName("Delete Clients: должен возвращать список ненайденных ИНН")
    void softDeleteClients_shouldReturnNotFoundInns() {
        List<String> rawInns = List.of("123", "999");

        when(innNormalizer.normalize(rawInns)).thenReturn(rawInns);

        Client client = new Client(1L, "123", "Test", false, null);
        when(clientRepository.findByInnIn(rawInns)).thenReturn(List.of(client));

        when(clientRepository.softDeleteActiveByInnIn(rawInns)).thenReturn(1);

        ClientDeleteResponse response = service.softDeleteClients(rawInns);

        assertThat(response.updatedCount()).isEqualTo(1);
        assertThat(response.notFoundInns()).containsExactly("999");
    }


    @Test
    @DisplayName("Get Balances: должен возвращать пустой ответ, если нет активных клиентов")
    void getActualClientBalances_shouldReturnEmptyListIfNoClients() {
        when(clientRepository.findActiveClients()).thenReturn(List.of());

        ClientBalancesResponse response = service.getActualClientBalances();

        assertThat(response.clientData()).isEmpty();
        assertThat(response.datetime()).isNotNull();
    }

    @Test
    @DisplayName("Get Balances: должен выбросить DataNotFound, если нет балансов")
    void getActualClientBalances_shouldThrowDataNotFoundIfNoBalances() {
        Client client = new Client(1L, "123", "Test", false, null);

        when(clientRepository.findActiveClients()).thenReturn(List.of(client));
        when(balanceRepository.findLatestBalancesByClientIds(List.of(1L))).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> service.getActualClientBalances());
    }

    @Test
    @DisplayName("Get Balances: должен корректно считать итоги и PlanBalance")
    void getActualClientBalances_shouldCalculateTotalsCorrectly() {
        LocalDateTime dt = LocalDateTime.now();
        Client client = new Client(1L, "123", "Test", false, null);
        Balance balance = Balance.builder().id(1L).client(client).balanceDateTime(dt)
                .currentBalance(new BigDecimal("1000")).morningBalance(new BigDecimal("500")).build();

        when(clientRepository.findActiveClients()).thenReturn(List.of(client));
        when(balanceRepository.findLatestBalancesByClientIds(List.of(1L))).thenReturn(List.of(balance));
        when(paymentRepository.findPaymentsForLatestBalances(List.of(1L))).thenReturn(List.of());

        PaymentAggregator.Totals totals = new PaymentAggregator.Totals(
                BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );
        when(paymentAggregator.aggregate(any())).thenReturn(totals);

        ClientBalancesResponse response = service.getActualClientBalances();

        assertThat(response.clientData()).hasSize(1);
        ClientBalanceDto dto = response.clientData().get(0);

        assertThat(dto.planBalance()).isEqualByComparingTo("990");
        assertThat(dto.externalPlannedLeave()).isEqualByComparingTo("10");
    }
}
