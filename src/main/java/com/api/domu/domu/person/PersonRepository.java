package com.api.domu.domu.person;

import com.api.domu.domu.shared.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    boolean existsByRut(String rut);

    boolean existsByRutAndIdNot(String rut, Long id);

    Page<PersonEntity> findByStatus(EntityStatus status, Pageable pageable);

    Page<PersonEntity> findByStatusAndRutContainingIgnoreCaseOrStatusAndFirstNameContainingIgnoreCaseOrStatusAndPaternalSurnameContainingIgnoreCase(
            EntityStatus rutStatus, String rut, EntityStatus firstNameStatus, String firstName,
            EntityStatus surnameStatus, String paternalSurname, Pageable pageable);

    Page<PersonEntity> findByRutContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrPaternalSurnameContainingIgnoreCase(
            String rut, String firstName, String paternalSurname, Pageable pageable);
}
