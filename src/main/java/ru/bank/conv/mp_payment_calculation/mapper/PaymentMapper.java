package ru.bank.conv.mp_payment_calculation.mapper;

import org.springframework.stereotype.Component;
import ru.bank.conv.mp_payment_calculation.dto.PaymentDto;
import ru.bank.conv.mp_payment_calculation.entity.Payment;

import java.util.List;

import static ru.bank.conv.mp_payment_calculation.constant.MapperConstants.*;

@Component
public class PaymentMapper {

    public List<PaymentDto> mapToDtoList(List<Payment> payments) {
        if (payments == null) {
            return List.of();
        }
        return payments.stream().map(this::toDto).toList();
    }

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(
                p.getAmount(),
                mapBoolean(p.getIsIncoming(), DIRECTION_INCOMING, DIRECTION_OUTGOING),
                mapPaymentTo(p),   // paymentTo
                mapPaymentFrom(p),   // paymentFrom
                mapBoolean(p.getIsExternal(), BANK_EXTERNAL, BANK_INTERNAL),
                mapBoolean(p.getIsExecuted(), STATUS_EXECUTED, STATUS_NOT_EXECUTED),
                safeString(p.getDescription())
        );
    }

    // Маппинг булевых значений по принципу true - одно значение / false - другое
    // Заменяет 3 одинаковых метода

    private String mapBoolean(Boolean value, String trueValue, String falseValue) {
        if (value == null) return NO_DATA;
        return value ? trueValue : falseValue;
    }

    /**
     * Логика "Куда" (paymentTo):
     * - Внутренний платеж: Совкомбанк
     * - Внешний входящий: Совкомбанк (деньги пришли к нам)
     * - Внешний исходящий: Корр. банк (деньги ушли туда)
     */
    private String mapPaymentTo(Payment p) {
        Boolean external = p.getIsExternal();
        Boolean incoming = p.getIsIncoming();

        if (external == null || incoming == null) return NO_DATA;

        // Если Внутренний или Входящий Внешний -> мы получатель
        if (!external || incoming) {
            return SOVCOMBANK;
        }

        // Иначе (Внешний Исходящий) -> получатель внешний банк
        return safeString(p.getRecipientBankName());
    }

    /**
     * Логика "Откуда" (paymentFrom):
     * - Внутренний платеж: Совкомбанк
     * - Внешний исходящий: Совкомбанк (деньги ушли от нас)
     * - Внешний входящий: Корр. банк (деньги пришли оттуда)
     */
    private String mapPaymentFrom(Payment p) {
        Boolean external = p.getIsExternal();
        Boolean incoming = p.getIsIncoming();

        if (external == null || incoming == null) return NO_DATA;

        // Если Внутренний или Исходящий Внешний -> мы отправитель
        if (!external || !incoming) {
            return SOVCOMBANK;
        }

        // Иначе (Внешний Входящий) -> отправитель внешний банк
        return safeString(p.getRecipientBankName());
    }

    private String safeString(String value) {
        return (value == null || value.isBlank()) ? NO_DATA : value;
    }

    /**
     * =====================================================================================================
     * Красиво заменяем 2 метода "Куда" + "Откуда" или понты какашки.
     * Определяет сторону перевода (Отправитель или Получатель).
     * Логика: Корр. банк нужен только если платеж Внешний (ext=true)
     * И направление (вход./исх.) НЕ совпадает с ролью (отправитель/получатель).
     *
     * @param isTargetReceiver true = ищем "Куда" (To), false = ищем "Откуда" (From)
     */

    private String determineCounterparty(Payment p, boolean isTargetReceiver) {
        Boolean external = p.getIsExternal();
        Boolean incoming = p.getIsIncoming();

        if (external == null || incoming == null) return NO_DATA;

        // Математика логики:
        // Для "To" (isTargetReceiver=true) банк нужен при Исходящем (inc=false). -> true != false -> true.
        // Для "From" (isTargetReceiver=false) банк нужен при Входящем (inc=true). -> false != true -> true.
        // В остальных случаях (внутренние или "наши" счета) -> Совкомбанк.

        boolean isCorrespondentBankNeeded = external && (isTargetReceiver != incoming);

        return isCorrespondentBankNeeded ? safeString(p.getRecipientBankName()) : SOVCOMBANK;
    }
}
