package ru.bank.conv.mp_payment_calculation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private Long integrationId;
    private String source;
}
