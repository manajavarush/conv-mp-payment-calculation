package ru.bank.conv.mp_payment_calculation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import ru.bank.conv.mp_payment_calculation.client.ConvUisGatewayClient;
import ru.bank.conv.mp_payment_calculation.config.GatewayProperties;
import ru.bank.conv.mp_payment_calculation.dto.*;
import ru.bank.conv.mp_payment_calculation.entity.Balance;
import ru.bank.conv.mp_payment_calculation.entity.Client;
import ru.bank.conv.mp_payment_calculation.entity.Payment;
import ru.bank.conv.mp_payment_calculation.exception.*;
import ru.bank.conv.mp_payment_calculation.mapper.PaymentMapper;
import ru.bank.conv.mp_payment_calculation.repository.BalanceRepository;
import ru.bank.conv.mp_payment_calculation.repository.ClientRepository;
import ru.bank.conv.mp_payment_calculation.repository.PaymentRepository;
import ru.bank.conv.mp_payment_calculation.util.InnNormalizer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMonitoringService {

    private final ClientRepository clientRepository;
    private final ConvUisGatewayClient gatewayClient;
    private final PaymentRepository paymentRepository;

    private final GatewayProperties properties;
    private final InnNormalizer innNormalizer;

    private final PaymentMapper paymentMapper;

    private final PaymentAggregator paymentAggregator;
    private final BalanceRepository balanceRepository;

    public GatewayResponse syncActiveClientsPayments() {
        var activeClientIds = clientRepository.findActiveClientIds();

        log.info("Syncing payments for {} active clients", activeClientIds.size());
        var request = buildGatewayRequest(activeClientIds);

        return executeGatewayRequest(request);
    }

    public GatewayResponse registerClientsForMonitoring(List<Long> requestedClientIds) {
        if (requestedClientIds == null || requestedClientIds.isEmpty()) {
            log.warn("Client IDs list cannot be null or empty");
            throw new BadRequestException();
        }

        var alreadyActiveIds = clientRepository.findActiveClientIdsByIdIn(requestedClientIds);

        if (!alreadyActiveIds.isEmpty()) {
            log.warn("Attempt to register already active clients: {}", alreadyActiveIds);
            throw new ClientAlreadyExistException();
        }

        log.info("Registering {} new clients", requestedClientIds.size());
        var request = buildGatewayRequest(requestedClientIds);
        return executeGatewayRequest(request);
    }

    @Transactional
    public ClientDeleteResponse softDeleteClients(List<String> rawInns) {
        var normalizedInns = innNormalizer.normalize(rawInns);

        if (normalizedInns.isEmpty()) {
            log.warn("No valid INNs to delete");
            return ClientDeleteResponse.success(0, List.of());
        }

        log.info("INNs normalized. originalCount={}, normalizedCount={}",
                rawInns.size(),
                normalizedInns.size()
        );

        try {
            var foundClients = clientRepository.findByInnIn(normalizedInns);

            Set<String> foundInns = foundClients.stream()
                    .map(Client::getInn)
                    .collect(Collectors.toSet());

            List<String> notFoundInns = normalizedInns.stream()
                    .filter(inn -> !foundInns.contains(inn))
                    .toList();

            int deletedCount = clientRepository.softDeleteActiveByInnIn(normalizedInns);

            log.info(
                    "Soft delete completed. deletedCount={}, notFoundCount={}",
                    deletedCount,
                    notFoundInns.size()
            );

            return ClientDeleteResponse.success(deletedCount, notFoundInns);

        } catch (DataAccessException exception) {
            log.error("Database error during soft delete clients", exception);
            throw new DbUnavailableException();
        }

    }

    @Transactional(readOnly = true)
    public ClientPaymentsResponse getClientPaymentsDetails(String rawInn, LocalDateTime dateTime) {
        log.info("Запрос детализации платежей: INN={}, DateTime={}", rawInn, dateTime);

        String inn = innNormalizer.normalizeSingle(rawInn)
                .orElseThrow(BadRequestException::new);

        try {
            Client client = clientRepository.findByInnAndDeletedFalse(inn)
                    .orElseThrow(PaymentsNotFoundException::new);

            List<Payment> payments = paymentRepository.findByClientIdAndPaymentDateTime(client.getId(), dateTime);

            if (payments.isEmpty()) {
                log.warn("Платежи не найдены для клиента INN={} за дату {}", inn, dateTime);
                throw new PaymentsNotFoundException();
            }

            ClientDataDto clientDataDto = new ClientDataDto(
                    client.getId(),
                    client.getInn(),
                    client.getName(),
                    paymentMapper.mapToDtoList(payments)
            );

            log.info("Детализация успешно сформирована для INN={}", inn);
            return new ClientPaymentsResponse(dateTime, clientDataDto);

        } catch (DataAccessException exception) {
            log.error("Database error while fetching payments for INN: {}", inn, exception);
            throw new DbUnavailableException();
        }
    }

    @Transactional(readOnly = true)
    public ClientBalancesResponse getActualClientBalances() {
        log.info("Запрос актуальных балансов клиентов");

        List<Client> activeClients = clientRepository.findActiveClients();

        if (activeClients.isEmpty()) {
            log.info("Активные клиенты не найдены, возвращаем пустой ответ");
            return ClientBalancesResponse.empty();
        }

        List<Long> clientIds = activeClients.stream().map(Client::getId).toList();

        List<Balance> latestBalances = balanceRepository.findLatestBalancesByClientIds(clientIds);

        if (latestBalances.isEmpty()) {
            log.warn("Балансы не найдены ни для одного из {} активных клиентов", activeClients.size());
            throw new DataNotFoundException();
        }

        Map<Long, Balance> balanceMap = latestBalances.stream()
                .collect(Collectors.toMap(b -> b.getClient().getId(), b -> b));

        List<Payment> paymentsForLatestBalances = paymentRepository.findPaymentsForLatestBalances(clientIds);

        Map<Long, List<Payment>> paymentsByClient = paymentsForLatestBalances.stream()
                .collect(Collectors.groupingBy(p -> p.getClient().getId()));

        List<ClientBalanceDto> clientDtos = activeClients.stream()
                .filter(client -> balanceMap.containsKey(client.getId()))
                .map(client -> buildClientBalanceDto(client, balanceMap, paymentsByClient))
                .toList();

        LocalDateTime responseTime = latestBalances.stream()
                .map(Balance::getBalanceDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        log.info("Сформирован ответ по балансам для {} клиентов. Дата среза: {}", clientDtos.size(), responseTime);
        return new ClientBalancesResponse(responseTime, clientDtos);
    }

    private ClientBalanceDto buildClientBalanceDto(Client client,
                                                   Map<Long, Balance> balanceMap,
                                                   Map<Long, List<Payment>> paymentsByClient) {

        Balance balance = balanceMap.get(client.getId());

        List<Payment> currentPayments = paymentsByClient.getOrDefault(client.getId(), List.of());

        PaymentAggregator.Totals totals = paymentAggregator.aggregate(currentPayments);

        BigDecimal planBalance = balance.getCurrentBalance()
                .subtract(totals.externalPlannedLeave())
                .subtract(totals.internalPlannedLeave());

        return new ClientBalanceDto(
                client.getInn(),
                client.getName(),
                balance.getMorningBalance(),
                balance.getCurrentBalance(),
                planBalance,
                totals.externalPlannedLeave(),
                totals.internalPlannedLeave(),
                totals.externalFactLeave(),
                totals.internalFactLeave(),
                totals.externalFactIncome(),
                totals.internalFactIncome()
        );
    }

    private GatewayResponse executeGatewayRequest(GatewayRequest request) {
        try {
            var response = gatewayClient.sendRequest(request);
            return response.getBody();
        } catch (HttpServerErrorException exception) {
            throw new UisUnavailableException();
        }
    }

    private GatewayRequest buildGatewayRequest(List<Long> clientIds) {
        return new GatewayRequest(
                properties.getIntegrationId(),
                new GatewayTask(clientIds),
                properties.getSource()
        );
    }
}
