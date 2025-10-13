package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bank.conv.mp_payment_calculation.entity.Client;

import java.util.Collection;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    String FIND_ACTIVE_CLIENT_IDS_FOR_UPDATE =
            "SELECT c.id FROM Client c WHERE c.isDeleted = false";

    String VALIDATE_CLIENT_IDS_FROM_REQUEST =
            "SELECT c.id FROM Client c WHERE c.isDeleted = false AND c.id IN :ids";

    // ФТ_1: Для эндпоинта обновления платежей
    @Query(FIND_ACTIVE_CLIENT_IDS_FOR_UPDATE)
    List<Long> findActiveClientIdsForUpdate();

    // ФТ_2: Для валидации клиентов из запроса на добавление
    @Query(VALIDATE_CLIENT_IDS_FROM_REQUEST)
    List<Long> validateClientIdsFromRequest(@Param("ids") Collection<Long> clientIdsFromRequest);
}