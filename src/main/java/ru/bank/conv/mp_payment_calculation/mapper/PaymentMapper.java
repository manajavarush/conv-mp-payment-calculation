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
                mapPaymentTo(p),
                mapPaymentFrom(p),
                mapBoolean(p.getIsExternal(), BANK_EXTERNAL, BANK_INTERNAL),
                mapBoolean(p.getIsExecuted(), STATUS_EXECUTED, STATUS_NOT_EXECUTED),
                safeString(p.getDescription())
        );
    }

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

        if (!external || incoming) {
            return SOVCOMBANK;
        }

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

        if (!external || !incoming) {
            return SOVCOMBANK;
        }

        return safeString(p.getRecipientBankName());
    }

    private String safeString(String value) {
        return (value == null || value.isBlank()) ? NO_DATA : value;
    }
}
