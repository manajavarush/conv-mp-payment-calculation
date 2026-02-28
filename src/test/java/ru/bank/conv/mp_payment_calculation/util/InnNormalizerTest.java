package ru.bank.conv.mp_payment_calculation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InnNormalizerTest {

    private final InnNormalizer normalizer = new InnNormalizer();

    @Test
    @DisplayName("normalize: должен вернуть пустой список при null входе")
    void normalize_shouldReturnEmptyList_whenInputNull() {
        assertThat(normalizer.normalize(null)).isEmpty();
    }

    @Test
    @DisplayName("normalize: должен фильтровать некорректные ИНН и очищать строку")
    void normalize_shouldFilterAndClean() {
        // Arrange: валидные (10, 12 знаков), с мусором, неверная длина
        List<String> rawInns = List.of(
                "7707083893",      // Валидный ЮЛ (10)
                "  7707083893  ",  // Валидный с пробелами
                "770708389301",    // Валидный ИП (12)
                "123",             // Невалидная длина
                "ABC7707083893DEF" // Валидный с буквами
        );

        // Act
        List<String> result = normalizer.normalize(rawInns);

        // Assert
        assertThat(result)
                .hasSize(2) // 2 уникальных валидных
                .containsExactlyInAnyOrder("7707083893", "770708389301");
    }

    @Test
    @DisplayName("normalize: должен убирать дубликаты")
    void normalize_shouldRemoveDuplicates() {
        List<String> rawInns = List.of("7707083893", "7707083893");

        List<String> result = normalizer.normalize(rawInns);

        assertThat(result).hasSize(1).containsExactly("7707083893");
    }

    // Используем MethodSource для передачи пар [вход, ожидаемый выход]
    private static Stream<Arguments> validInnProvider() {
        return Stream.of(
                Arguments.of("7707083893", "7707083893"),        // Чистый 10-значный
                Arguments.of("770708389301", "770708389301"),    // Чистый 12-значный
                Arguments.of(" 7707083     893  ", "7707083893"),      // С пробелами
                Arguments.of("AAA77CGR0708R3893B", "7707083893")       // С буквами
        );
    }

    @ParameterizedTest(name = "normalizeSingle: вход ''{0}'' -> ожидаем ''{1}''")
    @MethodSource("validInnProvider")
    @DisplayName("normalizeSingle: должен возвращать Optional с очищенным ИНН для валидных строк")
    void normalizeSingle_shouldReturnPresent_forValidInn(String input, String expected) {
        Optional<String> result = normalizer.normalizeSingle(input);

        assertThat(result)
                .isPresent()
                .contains(expected); // Проверяем конкретный ожидаемый результат
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "77070838930", ""})
    @NullAndEmptySource
    @DisplayName("normalizeSingle: должен возвращать empty для невалидных или null строк")
    void normalizeSingle_shouldReturnEmpty_forInvalidInn(String input) {
        assertThat(normalizer.normalizeSingle(input)).isEmpty();
    }
}
