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

        return Optional.ofNullable(rawInns)
                .orElseGet(List::of)
                .stream()
                .map(this::normalizeSingle)
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    public Optional<String> normalizeSingle(String rawInn) {

        return Optional.ofNullable(rawInn)
                .map(inn -> NON_DIGITS.matcher(inn).replaceAll(""))
                .filter(this::isValidInn);
    }

    private boolean isValidInn(String inn) {
        return inn.length() == INN_LEGAL_ENTITY_LENGTH || inn.length() == INN_INDIVIDUAL_LENGTH;
    }
}
