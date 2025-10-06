package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.conv.mp_payment_calculation.entity.Balance;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
