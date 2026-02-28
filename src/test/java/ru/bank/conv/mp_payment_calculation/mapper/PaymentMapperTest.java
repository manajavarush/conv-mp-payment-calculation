package ru.bank.conv.mp_payment_calculation.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bank.conv.mp_payment_calculation.dto.PaymentDto;
import ru.bank.conv.mp_payment_calculation.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {

    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    @DisplayName("mapToDtoList: должен возвращать пустой список при null")
    void mapToDtoList_shouldHandleNull() {
        assertThat(mapper.mapToDtoList(null)).isEmpty();
    }

    @Test
    @DisplayName("mapToDtoList: должен корректно маппить список")
    void mapToDtoList_shouldMapList() {
        Payment p1 = createPayment(BigDecimal.TEN, true, true, true, "Bank");
        List<PaymentDto> result = mapper.mapToDtoList(List.of(p1));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).amount()).isEqualByComparingTo("10");
    }

    @Test
    @DisplayName("Маппинг булевых полей: Status, Direction, OutBank")
    void mapBooleanFields() {
        Payment p = createPayment(null, true, true, false, null);

        PaymentDto dto = mapper.mapToDtoList(List.of(p)).get(0);

        assertThat(dto.status()).isEqualTo("Исполнен");
        assertThat(dto.outBank()).isEqualTo("Внешний");
        assertThat(dto.paymentDirection()).isEqualTo("Исходящий");
    }

    @Test
    @DisplayName("Маппинг булевых полей: Null -> 'Нет данных'")
    void mapBooleanFields_shouldHandleNull() {
        Payment p = createPayment(null, null, null, null, null);

        PaymentDto dto = mapper.mapToDtoList(List.of(p)).get(0);

        assertThat(dto.status()).isEqualTo("Нет данных");
        assertThat(dto.paymentDirection()).isEqualTo("Нет данных");
        assertThat(dto.outBank()).isEqualTo("Нет данных");
    }

    @Test
    @DisplayName("PaymentTo: Внутренний -> Совкомбанк")
    void mapPaymentTo_Internal() {
        Payment p = createPayment(null, null, false, false, null);

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentTo()).isEqualTo("Совкомбанк");
    }

    @Test
    @DisplayName("PaymentTo: Внешний Входящий -> Совкомбанк")
    void mapPaymentTo_ExternalIncoming() {
        Payment p = createPayment(null, null, true, true, null);

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentTo()).isEqualTo("Совкомбанк");
    }

    @Test
    @DisplayName("PaymentTo: Внешний Исходящий -> Корр. Банк")
    void mapPaymentTo_ExternalOutgoing() {
        Payment p = createPayment(null, null, true, false, "Sber");

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentTo()).isEqualTo("Sber");
    }

    @Test
    @DisplayName("PaymentTo: Внешний Исходящий без банка -> 'Нет данных'")
    void mapPaymentTo_ExternalOutgoing_NoBank() {
        Payment p = createPayment(null, null, true, false, null);

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentTo()).isEqualTo("Нет данных");
    }

    @Test
    @DisplayName("PaymentFrom: Внутренний -> Совкомбанк")
    void mapPaymentFrom_Internal() {
        Payment p = createPayment(null, null, false, true, null);

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentFrom()).isEqualTo("Совкомбанк");
    }

    @Test
    @DisplayName("PaymentFrom: Внешний Исходящий -> Совкомбанк")
    void mapPaymentFrom_ExternalOutgoing() {
        Payment p = createPayment(null, null, true, false, null);

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentFrom()).isEqualTo("Совкомбанк");
    }

    @Test
    @DisplayName("PaymentFrom: Внешний Входящий -> Корр. Банк")
    void mapPaymentFrom_ExternalIncoming() {
        Payment p = createPayment(null, null, true, true, "Alpha");

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentFrom()).isEqualTo("Alpha");
    }

    @Test
    @DisplayName("PaymentFrom: Внешний Входящий без банка -> 'Нет данных'")
    void mapPaymentFrom_ExternalIncoming_NoBank() {
        Payment p = createPayment(null, null, true, true, "   ");

        assertThat(mapper.mapToDtoList(List.of(p)).get(0).paymentFrom()).isEqualTo("Нет данных");
    }

    private Payment createPayment(BigDecimal amount, Boolean isExecuted, Boolean isExternal,
                                  Boolean isIncoming, String bankName) {
        Payment p = new Payment();
        p.setAmount(amount);
        p.setIsExecuted(isExecuted);
        p.setIsExternal(isExternal);
        p.setIsIncoming(isIncoming);
        p.setRecipientBankName(bankName);
        p.setDescription("Test");
        return p;
    }
}
