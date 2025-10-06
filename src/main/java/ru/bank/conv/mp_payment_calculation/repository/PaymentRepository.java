package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.conv.mp_payment_calculation.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
