package ru.bank.conv.mp_payment_calculation.constant;

public interface SwaggerMessages {

    String OK = "Выполнено успешно";
    String UIS_ERROR = "Ошибка на стороне UIS";
    String BAD_REQUEST = "Некорректный формат сообщения";
    String CLIENT_EXISTS = "Запрашиваемый Клиент уже есть в списке";

    String EXAMPLE_OK = "{\"message\": \"OK\"}";
    String EXAMPLE_BAD_REQUEST = "{\"message\": \"Bad Request\"}";
    String EXAMPLE_CLIENT_EXISTS = "{\"message\": \"Client already exists\"}";
    String EXAMPLE_UIS_ERROR = "{\"message\": \"UIS is not available\"}";
}
