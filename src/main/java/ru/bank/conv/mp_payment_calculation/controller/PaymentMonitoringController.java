package ru.bank.conv.mp_payment_calculation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.service.PaymentMonitoringService;

import java.util.List;

@RestController
@RequestMapping("/payments-monitoring")
@RequiredArgsConstructor
public class PaymentMonitoringController {

    private final PaymentMonitoringService paymentMonitoringService;

    /**
     * ФТ_1. GET /payments-monitoring/client-payments
     * Обновляет платежи: вызывает мок conv-uis-gateway через RestTemplate.
     */
    @GetMapping("/client-payments")
    public ResponseEntity<GatewayResponse> updateClientPayments() {
        var gatewayResponse = paymentMonitoringService.updatePayments();
        return ResponseEntity.ok(gatewayResponse);
    }

    @GetMapping("/clients")
    public ResponseEntity<GatewayResponse> addClients(@RequestParam("id") List<Long> ids) {
        var gatewayResponse = paymentMonitoringService.addClients(ids);
        return ResponseEntity.ok(gatewayResponse);
    }
}
