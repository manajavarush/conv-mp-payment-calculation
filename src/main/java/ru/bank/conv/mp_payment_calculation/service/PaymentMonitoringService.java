package ru.bank.conv.mp_payment_calculation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import ru.bank.conv.mp_payment_calculation.client.ConvUisGatewayClient;
import ru.bank.conv.mp_payment_calculation.config.GatewayProperties;
import ru.bank.conv.mp_payment_calculation.dto.GatewayRequest;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.dto.GatewayTask;
import ru.bank.conv.mp_payment_calculation.exception.BadRequestException;
import ru.bank.conv.mp_payment_calculation.exception.ClientAlreadyExistException;
import ru.bank.conv.mp_payment_calculation.exception.UisUnavailableException;
import ru.bank.conv.mp_payment_calculation.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMonitoringService {

    private final ClientRepository clientRepository;
    private final ConvUisGatewayClient gatewayClient;

    private final GatewayProperties properties;

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
        // 1) Если Клиент в базе есть, возвращаем ошибку 400 "Запрашиваемый Клиент уже есть в списке"
        // 2) Если is_deleted = false, тогда Клиент есть и не удален.
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
                // new GatewayTask(clientIds.toArray(new Long[0])),
                properties.getSource()
        );
    }
}
