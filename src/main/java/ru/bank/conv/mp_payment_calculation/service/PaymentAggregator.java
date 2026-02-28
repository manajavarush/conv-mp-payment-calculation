package ru.bank.conv.mp_payment_calculation.service;

import org.springframework.stereotype.Component;
import ru.bank.conv.mp_payment_calculation.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PaymentAggregator {

    public record Totals(
            BigDecimal externalPlannedLeave,
            BigDecimal internalPlannedLeave,
            BigDecimal externalFactLeave,
            BigDecimal internalFactLeave,
            BigDecimal externalFactIncome,
            BigDecimal internalFactIncome
    ) {
        public static Totals empty() {
            return new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    public Totals aggregate(List<Payment> payments) {

        if (payments.isEmpty()) {
            return Totals.empty();
        }

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

            if (status == null || outBank == null || direction == null || amount == null) {
                continue;
            }

            boolean executed = status;
            boolean external = outBank;
            boolean incoming = direction;

            if (!executed) {
                if (external) {
                    if (!incoming) extPlannedLeave = extPlannedLeave.add(amount);
                } else {
                    if (!incoming) intPlannedLeave = intPlannedLeave.add(amount);
                }

            } else {
                if (external) {
                    if (!incoming) extFactLeave = extFactLeave.add(amount);
                    else extFactIncome = extFactIncome.add(amount);
                } else {
                    if (!incoming) intFactLeave = intFactLeave.add(amount);
                    else intFactIncome = intFactIncome.add(amount);
                }
            }
        }

        return new Totals(extPlannedLeave, intPlannedLeave, extFactLeave,
                intFactLeave, extFactIncome, intFactIncome);
    }
}
