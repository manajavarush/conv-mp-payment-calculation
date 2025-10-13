package ru.bank.conv.mp_payment_calculation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.conv.mp_payment_calculation.constant.Message;
import ru.bank.conv.mp_payment_calculation.dto.GatewayRequest;
import ru.bank.conv.mp_payment_calculation.dto.GatewayResponse;
import ru.bank.conv.mp_payment_calculation.service.MockConvUisService;

@RestController
@RequestMapping("/v1/conv-uis-gateway")
@RequiredArgsConstructor
public class MockConvUisController {
    private final MockConvUisService mockConvUisService;

    // Указать для доки !? (consumes = MediaType.APPLICATION_JSON_VALUE,
    //                      produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping()
    public ResponseEntity<GatewayResponse> receive(@RequestBody GatewayRequest request) {
        var requestNumber = mockConvUisService.next();

        if (mockConvUisService.isFailure(requestNumber)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 status
                    .body(new GatewayResponse(Message.UIS_NOT_AVAILABLE.getText()));
        } else {
            return ResponseEntity.ok(new GatewayResponse(Message.OK.getText() + " " + request.task().toString())); // 200 status
        }
    }
}
