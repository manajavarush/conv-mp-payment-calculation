package ru.bank.conv.mp_payment_calculation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockConvUisServiceTest {

    private final MockConvUisService service = new MockConvUisService();

    @Test
    @DisplayName("Первые 4 запроса должны быть успешными")
    void firstFourRequestsShouldBeSuccessful() {
        for (int i = 0; i < 4; i++) {
            long num = service.next();
            boolean failure = service.isFailure(num);
            assertThat(failure).isFalse();
        }
    }

    @Test
    @DisplayName("Каждый 5-й запрос должен возвращать ошибку")
    void fifthRequestShouldFail() {

        for (int i = 0; i < 4; i++) {
            service.next();
        }

        long num = service.next();
        assertThat(num).isEqualTo(5);
        assertThat(service.isFailure(num)).isTrue();
    }

    @Test
    @DisplayName("10-й запрос тоже должен падать")
    void tenthRequestShouldFail() {

        for (int i = 0; i < 9; i++) {
            service.next();
        }

        long num = service.next();
        assertThat(num).isEqualTo(10);
        assertThat(service.isFailure(num)).isTrue();
    }
}
