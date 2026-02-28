package ru.bank.conv.mp_payment_calculation.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class InnNormalizer {
    private static final int INN_LEGAL_ENTITY_LENGTH = 10;
    private static final int INN_INDIVIDUAL_LENGTH = 12;
    private static final Pattern NON_DIGITS = Pattern.compile("\\D");

    public List<String> normalize(List<String> rawInns) {

        // Optional.ofNullable(rawInns).orElseGet(List::of) -> оборачиваем List в Optional
        // Если List == null, то получаем Optional.empty -> создаем пустой неизменяемый List
        // Иначе возвращаем исходный список "orElseGet(List::of)" supplier НЕ выполняется

        return Optional.ofNullable(rawInns)
                .orElseGet(List::of)

                // превращаем список в стрим для удобства работы в функциональном стиле
                .stream()

                // преобразуем Stream<String> в Stream<Optional<String>> (содержит нормализованные и пустые Optional)
                .map(this::normalizeSingle)

                // преобразуем в Stream<String> который содержит только корректные значения. Optional.empty игнорируются
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    public Optional<String> normalizeSingle(String rawInn) {

        return Optional.ofNullable(rawInn)
                .map(inn -> NON_DIGITS.matcher(inn).replaceAll(""))
                .filter(this::isValidInn);
    }

    // Бизнес-валидация (исходя из требований) => длина 10 (юр.лица) / длина 12 (физ.лица)
    private boolean isValidInn(String inn) {
        return inn.length() == INN_LEGAL_ENTITY_LENGTH || inn.length() == INN_INDIVIDUAL_LENGTH;
    }
}
