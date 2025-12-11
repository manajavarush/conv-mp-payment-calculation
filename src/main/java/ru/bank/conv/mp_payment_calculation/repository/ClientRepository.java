package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bank.conv.mp_payment_calculation.entity.Client;

import java.util.Collection;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    String FIND_ACTIVE_CLIENT_IDS =
            "SELECT c.id FROM Client c WHERE c.isDeleted = false";

    String FIND_ACTIVE_CLIENT_IDS_BY_ID_IN =
            "SELECT c.id FROM Client c WHERE c.isDeleted = false AND c.id IN :ids";

    // ФТ_1: Для эндпоинта обновления платежей
    @Query(FIND_ACTIVE_CLIENT_IDS)
    List<Long> findActiveClientIds();

    // ФТ_2: Этот запрос вернет ID клиентов, которые уже есть и активны
    @Query(FIND_ACTIVE_CLIENT_IDS_BY_ID_IN)
    List<Long> findActiveClientIdsByIdIn(@Param("ids") Collection<Long> clientIdsFromRequest);
}
