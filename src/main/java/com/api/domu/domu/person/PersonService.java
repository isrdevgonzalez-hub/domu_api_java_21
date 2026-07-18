package com.api.domu.domu.person;

import com.api.domu.domu.shared.DomainException;
import com.api.domu.domu.shared.EntityStatus;
import com.api.domu.domu.shared.PageResponse;
import com.api.domu.domu.shared.RutUtils;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public PersonResponse create(CreatePersonRequest request) {
        String normalizedRut = normalizeOptionalRut(request.rut());
        ensureUniqueRut(normalizedRut, null);
        PersonEntity entity = new PersonEntity();
        mapRequest(entity, request.firstName(), request.paternalSurname(), request.maternalSurname(), normalizedRut,
                request.email(), request.phone(), request.positionName(), request.origin());
        entity.setStatus(EntityStatus.ACTIVE);
        return toResponse(personRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PageResponse<PersonResponse> list(String term, EntityStatus status, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort, "firstName"));
        Page<PersonEntity> result;
        if (term != null && !term.isBlank() && status != null) {
            result = personRepository
                    .findByStatusAndRutContainingIgnoreCaseOrStatusAndFirstNameContainingIgnoreCaseOrStatusAndPaternalSurnameContainingIgnoreCase(
                            status, term.trim(), status, term.trim(), status, term.trim(), pageable);
        } else if (term != null && !term.isBlank()) {
            result = personRepository
                    .findByRutContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrPaternalSurnameContainingIgnoreCase(
                            term.trim(), term.trim(), term.trim(), pageable);
        } else if (status != null) {
            result = personRepository.findByStatus(status, pageable);
        } else {
            result = personRepository.findAll(pageable);
        }
        return new PageResponse<>(result.map(this::toResponse).getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PersonResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public PersonResponse update(Long id, UpdatePersonRequest request) {
        PersonEntity entity = findById(id);
        String normalizedRut = normalizeOptionalRut(request.rut());
        ensureUniqueRut(normalizedRut, id);
        mapRequest(entity, request.firstName(), request.paternalSurname(), request.maternalSurname(), normalizedRut,
                request.email(), request.phone(), request.positionName(), request.origin());
        return toResponse(personRepository.save(entity));
    }

    @Transactional
    public PersonResponse changeStatus(Long id, EntityStatus status) {
        PersonEntity entity = findById(id);
        entity.setStatus(status);
        return toResponse(personRepository.save(entity));
    }

    public PersonEntity findActiveEntity(Long id) {
        PersonEntity entity = findById(id);
        if (entity.getStatus() != EntityStatus.ACTIVE) {
            throw new DomainException("PERSON_INACTIVE", "La persona indicada se encuentra inactiva.");
        }
        return entity;
    }

    public PersonEntity findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new DomainException("PERSON_NOT_FOUND", "No se encontro la persona indicada."));
    }

    private void ensureUniqueRut(String rut, Long id) {
        if (rut == null) {
            return;
        }
        boolean exists = id == null ? personRepository.existsByRut(rut) : personRepository.existsByRutAndIdNot(rut, id);
        if (exists) {
            throw new DomainException("PERSON_RUT_ALREADY_EXISTS",
                    "Ya existe una persona registrada con el RUT indicado.");
        }
    }

    private String normalizeOptionalRut(String rut) {
        if (rut == null || rut.isBlank()) {
            return null;
        }
        if (!RutUtils.isValid(rut)) {
            throw new DomainException("INVALID_PERSON_RUT", "El RUT de la persona no es valido.");
        }
        return RutUtils.normalize(rut);
    }

    private void mapRequest(PersonEntity entity, String firstName, String paternalSurname, String maternalSurname,
                            String rut, String email, String phone, String positionName, PersonOrigin origin) {
        entity.setFirstName(clean(firstName));
        entity.setPaternalSurname(clean(paternalSurname));
        entity.setMaternalSurname(cleanNullable(maternalSurname));
        entity.setRut(rut);
        entity.setEmail(normalizeEmail(email));
        entity.setPhone(cleanNullable(phone));
        entity.setPositionName(cleanNullable(positionName));
        entity.setOrigin(origin);
    }

    private PersonResponse toResponse(PersonEntity entity) {
        return new PersonResponse(entity.getId(), entity.getFirstName(), entity.getPaternalSurname(),
                entity.getMaternalSurname(), entity.getRut(), entity.getEmail(), entity.getPhone(),
                entity.getPositionName(), entity.getOrigin(), entity.getStatus(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private String normalizeEmail(String email) {
        return Optional.ofNullable(email)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(String::toLowerCase)
                .orElse(null);
    }

    private String clean(String value) {
        String cleaned = cleanNullable(value);
        if (cleaned == null) {
            throw new DomainException("VALIDATION_ERROR", "El campo es obligatorio.");
        }
        return cleaned;
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }

    private Sort buildSort(String sort, String defaultProperty) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(defaultProperty).ascending();
        }
        String[] tokens = sort.split(",");
        return Sort.by(tokens.length > 1 && "desc".equalsIgnoreCase(tokens[1]) ? Sort.Direction.DESC : Sort.Direction.ASC,
                tokens[0]);
    }
}
