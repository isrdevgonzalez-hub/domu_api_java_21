package com.api.domu.domu.client;

import com.api.domu.domu.shared.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByRut(String rut);

    boolean existsByRutAndIdNot(String rut, Long id);

    Page<ClientEntity> findByStatus(EntityStatus status, Pageable pageable);

    Page<ClientEntity> findByRutContainingIgnoreCaseOrBusinessNameContainingIgnoreCase(String rut, String businessName,
                                                                                       Pageable pageable);

    Page<ClientEntity> findByStatusAndRutContainingIgnoreCaseOrStatusAndBusinessNameContainingIgnoreCase(
            EntityStatus leftStatus, String rut, EntityStatus rightStatus, String businessName, Pageable pageable);
}
