package ru.bank.conv.mp_payment_calculation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * Глобальная конфигурация Swagger / OpenAPI 3.0.
 * Красивое отображение заголовка, версии проекта, его описания
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Conv MP Payment Calculation API",
                version = "1.0",
                description = "Учебный проект: эмуляция интеграции с conv-uis-gateway"
        )
)
@Configuration
public class OpenApiConfig {
}
