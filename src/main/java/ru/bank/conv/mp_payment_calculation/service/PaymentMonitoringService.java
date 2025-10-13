package ru.bank.conv.mp_payment_calculation.service;

import lombok.RequiredArgsConstructor;
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
public class PaymentMonitoringService {

    private final ClientRepository repository;
    private final ConvUisGatewayClient client;

    private final GatewayProperties properties;

    public GatewayResponse updatePayments() {

        // Получить коллекцию id активных клиентов (isDeleted = false) из БД
        var activeClientIds = repository.findActiveClientIdsForUpdate();

        var request = buildGatewayRequest(activeClientIds);

        return executeGatewayRequest(request);

    }

    public GatewayResponse addClients(List<Long> ids) {

        // Валидация формата входных данных (параметров запроса: null, пустой список)
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException();
        }

        // Валидация данных на предмет добавления уже существующих клиентов
        var activeClientIds = repository.validateClientIdsFromRequest(ids);

        // Перед формированием JSON реализуем проверки:
        // 1) Если Клиент в базе есть, возвращаем ошибку 400 "Запрашиваемый Клиент уже есть в списке"
        // 2) Если is_deleted = false, тогда Клиент есть и не удален.
        // Возвращаем ошибку 400 "Запрашиваемый Клиент уже есть в списке"

        // иначе мы обращаемся во внешний сервис

        // Если клиент уже есть в БД
        if (!activeClientIds.isEmpty()) {
            throw new ClientAlreadyExistException();
        } else {
            // Иначе передаем в request оригинальные параметры запросы (id-s)
            var request = buildGatewayRequest(ids);
            return executeGatewayRequest(request);
        }
    }

    private GatewayResponse executeGatewayRequest(GatewayRequest request) {
        // Делаем REST-вызов в мок conv-uis-gateway и возвращаем тело ответа
        try {
            var response = client.sendRequest(request);
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
