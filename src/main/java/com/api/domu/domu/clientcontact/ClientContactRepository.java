package com.api.domu.domu.clientcontact;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientContactRepository extends JpaRepository<ClientContactEntity, Long> {

    boolean existsByClientIdAndPersonId(Long clientId, Long personId);

    List<ClientContactEntity> findByClientId(Long clientId);

    Optional<ClientContactEntity> findByIdAndClientId(Long id, Long clientId);

    Optional<ClientContactEntity> findByClientIdAndPrimaryContactTrueAndStatus(Long clientId,
                                                                               com.api.domu.domu.shared.EntityStatus status);
}
