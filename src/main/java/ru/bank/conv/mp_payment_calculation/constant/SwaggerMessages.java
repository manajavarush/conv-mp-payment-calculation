package ru.bank.conv.mp_payment_calculation.constant;

public interface SwaggerMessages {

    String OK = "Выполнено успешно";
    String UIS_ERROR = "Ошибка на стороне UIS";
    String BAD_REQUEST = "Некорректный формат сообщения";
    String CLIENT_EXISTS = "Запрашиваемый Клиент уже есть в списке";
    String DB_ERROR = "Ошибка базы данных";
    String PAYMENTS_NOT_FOUND = "Платежи не найдены";
    String DATA_NOT_FOUND = "Данные не найдены";

    String EXAMPLE_OK = "{\"message\": \"OK\"}";
    String EXAMPLE_BAD_REQUEST = "{\"message\": \"Bad Request\"}";
    String EXAMPLE_CLIENT_EXISTS = "{\"message\": \"Client already exists\"}";
    String EXAMPLE_UIS_ERROR = "{\"message\": \"UIS is not available\"}";
    String EXAMPLE_DB_ERROR = "{\"message\": \"DB is not available\"}";

    String EXAMPLE_PAYMENTS_NOT_FOUND = "{\"message\": \"Not found payments\"}";

    String EXAMPLE_DATA_NOT_FOUND = "{\"message\": \"There is no data available. It is necessary to send a request to Inversion\"}";

    String EXAMPLE_CLIENT_BALANCES = """
            {
                "datetime": "05.07.2024T09:37:12",
                "clientData":
                [
                    {
                        "inn": 733537281819,
                        "name": " ",
                        "incomeBalance": 25657890.00,
                        "currentBalance": 20345740.14,
                        "planBalance": 17845740.14,
                        "externalPlannedLeave": 2500000.00,
                        "internalPlannedLeave": 0,
                        "externalFactLeave": 1256347.56,
                        "internalFactLeave": 3428150.34,
                        "externalFactIncome": 0,
                        "internalFactIncome": 3140347.08
                    },
                    {
                        "inn": 733537281819,
                        "name": " ",
                        "incomeBalance": 25657890.00,
                        "currentBalance": 20345740.14,
                        "planBalance": 17845740.14,
                        "externalPlannedLeave": 2500000.00,
                        "internalPlannedLeave": 0,
                        "externalFactLeave": 1256347.56,
                        "internalFactLeave": 3428150.34,
                        "externalFactIncome": 0,
                        "internalFactIncome": 3140347.08
                    }
                ]
             }
            """;

    String EXAMPLE_CLIENT_PAYMENTS = """
            {
              "dateTime": "2024-01-01T09:00:00",
              "clientData": {
                "id": 1,
                "inn": "770708389300",
                "name": "ООО Альфа",
                "payments": [
                  {
                    "amount": 100.00,
                    "paymentDirection": "Исходящий",
                    "paymentTo": "Sberbank",
                    "paymentFrom": "Совкомбанк",
                    "outBank": "Внешний",
                    "status": "Не исполнен",
                    "description": "Оплата договора"
                  }
                ]
              }
            }
            """;
}
