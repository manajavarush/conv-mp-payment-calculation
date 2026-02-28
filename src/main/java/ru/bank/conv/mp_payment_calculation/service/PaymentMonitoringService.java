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
        // Получить коллекцию id активных клиентов (isDeleted = false) из БД
        var activeClientIds = clientRepository.findActiveClientIds();

        log.info("Syncing payments for {} active clients", activeClientIds.size());
        var request = buildGatewayRequest(activeClientIds);

        return executeGatewayRequest(request);
    }

    public GatewayResponse registerClientsForMonitoring(List<Long> requestedClientIds) {
        // Валидация формата входных данных (параметров запроса: null, пустой список)
        if (requestedClientIds == null || requestedClientIds.isEmpty()) {
            log.warn("Client IDs list cannot be null or empty");
            throw new BadRequestException();
        }

        // Валидация данных на предмет добавления уже существующих клиентов
        var alreadyActiveIds = clientRepository.findActiveClientIdsByIdIn(requestedClientIds);

        // Перед формированием JSON реализуем проверки:
        // 1. Если Клиент в базе есть, возвращаем ошибку 400 "Запрашиваемый Клиент уже есть в списке"
        // 2. Если is_deleted = false, тогда Клиент есть и не удален.
        // Возвращаем ошибку 400 "Запрашиваемый Клиент уже есть в списке"

        // иначе мы обращаемся во внешний сервис

        // Если клиент уже есть в БД
        if (!alreadyActiveIds.isEmpty()) {
            log.warn("Attempt to register already active clients: {}", alreadyActiveIds);
            throw new ClientAlreadyExistException();
        }
        // По идее здесь нужно "воскрешать" старые записи, если она уже есть в БД, но удалена isDeleted = true
        // ответ аналитика - не важно, соответственно забиваем болт на восстановление записей

        // Иначе передаем в request оригинальные параметры запросы (id-s)

        log.info("Registering {} new clients", requestedClientIds.size());
        var request = buildGatewayRequest(requestedClientIds);
        return executeGatewayRequest(request);
    }

    @Transactional
    public ClientDeleteResponse softDeleteClients(List<String> rawInns) {
        var normalizedInns = innNormalizer.normalize(rawInns);

        // Если после валидации список окажется пуст, нет смысла дальше с ним работать (early return)
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

            // валидные ИНН, соответствующие клиентам в БД
            Set<String> foundInns = foundClients.stream()
                    .map(Client::getInn)
                    .collect(Collectors.toSet());

            // валидные ИНН, которые отсутствуют в БД
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

        // 1. Нормализация ИНН
        String inn = innNormalizer.normalizeSingle(rawInn)
                .orElseThrow(BadRequestException::new);

        // Работа с БД
        try {

            // 2. Поиск клиента (сразу отсекаем удаленных через репозиторий)
            Client client = clientRepository.findByInnAndDeletedFalse(inn)
                    .orElseThrow(PaymentsNotFoundException::new);
            // Если клиент удален или не найден -> 404 (платежей нет)

            // 3. Поиск платежей
            List<Payment> payments = paymentRepository.findByClientIdAndPaymentDateTime(client.getId(), dateTime);

            if (payments.isEmpty()) {
                log.warn("Платежи не найдены для клиента INN={} за дату {}", inn, dateTime);
                throw new PaymentsNotFoundException();
            }

            // 4. Маппинг (через MapStruct или ручной)
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

        // 1. Получаем активных клиентов
        List<Client> activeClients = clientRepository.findActiveClients();

        if (activeClients.isEmpty()) {
            log.info("Активные клиенты не найдены, возвращаем пустой ответ");
            return ClientBalancesResponse.empty();
        }

        List<Long> clientIds = activeClients.stream().map(Client::getId).toList();

        // 2. Балансы
        List<Balance> latestBalances = balanceRepository.findLatestBalancesByClientIds(clientIds);

        if (latestBalances.isEmpty()) {
            log.warn("Балансы не найдены ни для одного из {} активных клиентов", activeClients.size());
            throw new DataNotFoundException();
        }

        Map<Long, Balance> balanceMap = latestBalances.stream()
                .collect(Collectors.toMap(b -> b.getClient().getId(), b -> b));

        // 3. Получаем только нужные платежи из БД
        List<Payment> paymentsForLatestBalances = paymentRepository.findPaymentsForLatestBalances(clientIds);

        // Группируем
        Map<Long, List<Payment>> paymentsByClient = paymentsForLatestBalances.stream()
                .collect(Collectors.groupingBy(p -> p.getClient().getId()));

        // 4. Сборка результата
        List<ClientBalanceDto> clientDtos = activeClients.stream()
                .filter(client -> balanceMap.containsKey(client.getId())) // Пропускаем тех, у кого нет баланса
                .map(client -> buildClientBalanceDto(client, balanceMap, paymentsByClient))
                .toList();

        // 5. Формирование ответа
        LocalDateTime responseTime = latestBalances.stream()
                .map(Balance::getBalanceDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        log.info("Сформирован ответ по балансам для {} клиентов. Дата среза: {}", clientDtos.size(), responseTime);
        return new ClientBalancesResponse(responseTime, clientDtos);
    }


    /**
     * Собирает DTO для одного конкретного клиента.
     */
    private ClientBalanceDto buildClientBalanceDto(Client client,
                                                   Map<Long, Balance> balanceMap,
                                                   Map<Long, List<Payment>> paymentsByClient) {

        Balance balance = balanceMap.get(client.getId());

        // Фильтрация платежей по дате баланса

//        List<Payment> currentPayments = paymentsByClient.getOrDefault(client.getId(), List.of())
//                .stream()
//                .filter(p -> p.getDateTime().equals(balance.getBalanceDateTime()))
//                .toList();

        // Уже отфильтрованы по дате SQL-запросом
        List<Payment> currentPayments = paymentsByClient.getOrDefault(client.getId(), List.of());

        // Делегируем математику агрегатору
        PaymentAggregator.Totals totals = paymentAggregator.aggregate(currentPayments);

        // Расчет planBalance по формуле из ТЗ
        BigDecimal planBalance = balance.getCurrentBalance()
                .subtract(totals.externalPlannedLeave())
                .subtract(totals.internalPlannedLeave());

        // Собираем и возвращаем DTO
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
        // Делаем REST-вызов в мок conv-uis-gateway и возвращаем тело ответа
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
