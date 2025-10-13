package ru.bank.conv.mp_payment_calculation.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.bank.conv.mp_payment_calculation.dto.GatewayRequest;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;

@Component
@RequiredArgsConstructor
public class ConvUisGatewayClient {

    private final RestTemplate restTemplate;

    @Value("${gateway.url}")
    private String gatewayUrl;

    public ResponseEntity<GatewayResponse> sendRequest(GatewayRequest request) {
        return restTemplate.postForEntity(gatewayUrl, request, GatewayResponse.class);
    }
}
