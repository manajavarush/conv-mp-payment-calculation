package ru.bank.conv.mp_payment_calculation.service;

import org.springframework.stereotype.Component;
import ru.bank.conv.mp_payment_calculation.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PaymentAggregator {

    public record Totals(
            BigDecimal externalPlannedLeave, //  внешние планируемые списания (НЕ исполненные)
            BigDecimal internalPlannedLeave, // внутренние планируемые списания (НЕ исполненные)
            BigDecimal externalFactLeave, // внешние фактические списания (исполненные)
            BigDecimal internalFactLeave, // внутренние фактические списания (исполненные)
            BigDecimal externalFactIncome, // внешние фактические поступления
            BigDecimal internalFactIncome // внутренние фактические поступления
    ) {
        // Статический фабричный метод - когда платежей нет или все они проигнорированы (null-поля)
        public static Totals empty() {
            return new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    // Принимаем платежи - возвращаем суммированные данные (объект Totals)
    public Totals aggregate(List<Payment> payments) {

        // Платежей нет - суммировать нечего
        if (payments.isEmpty()) {
            return Totals.empty();
        }

        // Накапливаем суммы в обычных переменных
        // т.к. record-s immutable, не можем использовать Totals.empty

        BigDecimal extPlannedLeave = BigDecimal.ZERO;
        BigDecimal intPlannedLeave = BigDecimal.ZERO;
        BigDecimal extFactLeave = BigDecimal.ZERO;
        BigDecimal intFactLeave = BigDecimal.ZERO;
        BigDecimal extFactIncome = BigDecimal.ZERO;
        BigDecimal intFactIncome = BigDecimal.ZERO;

        for (Payment p : payments) {
            Boolean status = p.getIsExecuted();
            Boolean outBank = p.getIsExternal();
            Boolean direction = p.getIsIncoming();
            BigDecimal amount = p.getAmount();

            // 1. Проверяем на null, если хотя бы 1 поле null - пропускаем платеж
            // Ответ аналитика - возвращаем "Нет данных", не нужно интерпретировать NULL как false и т.д.
            if (status == null || outBank == null || direction == null || amount == null) {
                continue;
            }

            // 2. Распаковываем в примитивы (NPE здесь невозможен благодаря проверке выше)
            // Иначе пришлось бы каждый раз проверять на null или использовать метод Boolean.TRUE.equals(status)
            // При этом status == false и при реальном false, и при null (методу все равно)
            boolean executed = status;
            boolean external = outBank;
            boolean incoming = direction;

            // 3. Бизнес логика

            // Не исполнен
            if (!executed) {
                // Внешние
                if (external) {
                    // Исходящие
                    if (!incoming) extPlannedLeave = extPlannedLeave.add(amount);
                } else {
                    // Внутренние исходящие
                    if (!incoming) intPlannedLeave = intPlannedLeave.add(amount);
                }

            // Исполнен
            } else {
                // Внешние
                if (external) {
                    if (!incoming) extFactLeave = extFactLeave.add(amount); // исходящие
                    else extFactIncome = extFactIncome.add(amount); // входящие
                } else {
                    // Внутренние
                    if (!incoming) intFactLeave = intFactLeave.add(amount); // исходящие
                    else intFactIncome = intFactIncome.add(amount); // входящие
                }
            }
        }

        return new Totals(extPlannedLeave, intPlannedLeave, extFactLeave,
                intFactLeave, extFactIncome, intFactIncome);
    }
}
