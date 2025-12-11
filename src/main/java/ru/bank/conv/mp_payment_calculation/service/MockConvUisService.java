package ru.bank.conv.mp_payment_calculation.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Эмулирует поведение внешнего сервиса UIS:
 * каждый 5-й запрос — ошибка 500, остальные — 200 OK.
 */

@Service
public class MockConvUisService {
    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Возвращает номер запроса.
     */
    public long next() {
        return counter.incrementAndGet();
    }

    /**
     * Проверяет, является ли запрос неуспешным (каждый 5-й).
     */
    public boolean isFailure(Long number) {
        return number % 5 == 0;
    }
}
