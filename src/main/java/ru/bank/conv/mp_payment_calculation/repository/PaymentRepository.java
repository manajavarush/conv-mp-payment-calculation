package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bank.conv.mp_payment_calculation.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    String FIND_BY_CLIENT_ID_AND_PAYMENT_DATETIME = """
            SELECT p FROM Payment p
            WHERE p.client.id = :clientId
            AND p.dateTime = :dateTime
            """;

    String FIND_PAYMENTS_FOR_LATEST_BALANCES = """
            SELECT p FROM Payment p
            JOIN Balance b ON p.client.id = b.client.id
                           AND p.dateTime = b.balanceDateTime
            WHERE b.client.id IN :clientIds
              AND b.balanceDateTime = (
                  SELECT MAX(b2.balanceDateTime)
                  FROM Balance b2
                  WHERE b2.client.id = b.client.id
              )
            """;

    @Query(FIND_BY_CLIENT_ID_AND_PAYMENT_DATETIME)
    List<Payment> findByClientIdAndPaymentDateTime(@Param("clientId") Long clientId,
                                                   @Param("dateTime") LocalDateTime dateTime);

    @Query(FIND_PAYMENTS_FOR_LATEST_BALANCES)
    List<Payment> findPaymentsForLatestBalances(@Param("clientIds") List<Long> clientIds);
}
