package ru.bank.conv.mp_payment_calculation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bank.conv.mp_payment_calculation.entity.Client;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    String FIND_ACTIVE_CLIENT_IDS =
            "SELECT c.id FROM Client c WHERE c.deleted = false";

    String FIND_ACTIVE_CLIENT_IDS_BY_ID_IN =
            "SELECT c.id FROM Client c WHERE c.deleted = false AND c.id IN :ids";

    String SOFT_DELETE_ACTIVE_BY_INN_IN = """
            UPDATE Client client
                SET client.deleted = true
            WHERE client.inn IN :inns
                AND client.deleted = false
            """;

    String FIND_BY_INN_IN = "SELECT c FROM Client c WHERE c.inn IN :inns";
    String FIND_BY_INN_AND_DELETED_FALSE = "SELECT c FROM Client c WHERE c.inn = :inn AND c.deleted = false";

    String FIND_ACTIVE_CLIENTS = "SELECT c FROM Client c WHERE c.deleted = false";

    @Query(FIND_ACTIVE_CLIENT_IDS)
    List<Long> findActiveClientIds();

    @Query(FIND_ACTIVE_CLIENT_IDS_BY_ID_IN)
    List<Long> findActiveClientIdsByIdIn(@Param("ids") Collection<Long> clientIdsFromRequest);

    @Modifying
    @Query(SOFT_DELETE_ACTIVE_BY_INN_IN)
    int softDeleteActiveByInnIn(@Param("inns") Collection<String> inns);

    @Query(FIND_BY_INN_IN)
    List<Client> findByInnIn(@Param("inns") Collection<String> inns);

    @Query(FIND_BY_INN_AND_DELETED_FALSE)
    Optional<Client> findByInnAndDeletedFalse(@Param("inn") String inn);

    @Query(FIND_ACTIVE_CLIENTS)
    List<Client> findActiveClients();
}
