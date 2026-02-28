package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bank.conv.mp_payment_calculation.entity.Balance;

import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    String FIND_LATEST_BALANCES_BY_CLIENT_IDS = """
            SELECT b FROM Balance b
            WHERE b.client.id IN :clientIds
            AND b.balanceDateTime = (
                SELECT MAX(b2.balanceDateTime) FROM Balance b2
                WHERE b2.client.id = b.client.id
            )
            """;

    @Query(FIND_LATEST_BALANCES_BY_CLIENT_IDS)
    List<Balance> findLatestBalancesByClientIds(@Param("clientIds") List<Long> clientIds);
}
