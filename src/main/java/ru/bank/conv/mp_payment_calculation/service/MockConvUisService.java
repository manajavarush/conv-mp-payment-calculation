package ru.bank.conv.mp_payment_calculation.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Эмулирует поведение UIS Gateway: 4 успешных ответа, 5-й — 500.
 */

@Service
public class MockConvUisService {
    private final AtomicLong counter = new AtomicLong(0);

    public long next() {
        return counter.incrementAndGet();
    }

    public boolean isFailure(Long number) {
        return number % 5 == 0;
    }

}
