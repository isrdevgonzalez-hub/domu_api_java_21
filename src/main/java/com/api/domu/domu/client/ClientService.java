package com.api.domu.domu.client;

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
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        String rut = normalizeRequiredRut(request.rut(), "INVALID_CLIENT_RUT");
        if (clientRepository.existsByRut(rut)) {
            throw new DomainException("CLIENT_RUT_ALREADY_EXISTS",
                    "Ya existe un cliente registrado con el RUT indicado.");
        }
        ClientEntity entity = new ClientEntity();
        mapRequest(entity, request.clientType(), request.businessName(), rut, request.description(),
                request.businessActivity(), request.email(), request.phone(), request.address(), request.district(),
                request.city(), request.region(), request.postalCode());
        entity.setStatus(EntityStatus.ACTIVE);
        return toResponse(clientRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PageResponse<ClientResponse> list(String term, EntityStatus status, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort, "businessName"));
        Page<ClientEntity> result;
        if (term != null && !term.isBlank() && status != null) {
            result = clientRepository.findByStatusAndRutContainingIgnoreCaseOrStatusAndBusinessNameContainingIgnoreCase(
                    status, term.trim(), status, term.trim(), pageable);
        } else if (term != null && !term.isBlank()) {
            result = clientRepository.findByRutContainingIgnoreCaseOrBusinessNameContainingIgnoreCase(
                    term.trim(), term.trim(), pageable);
        } else if (status != null) {
            result = clientRepository.findByStatus(status, pageable);
        } else {
            result = clientRepository.findAll(pageable);
        }
        return new PageResponse<>(result.map(this::toResponse).getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request) {
        ClientEntity entity = findById(id);
        String rut = normalizeRequiredRut(request.rut(), "INVALID_CLIENT_RUT");
        if (clientRepository.existsByRutAndIdNot(rut, id)) {
            throw new DomainException("CLIENT_RUT_ALREADY_EXISTS",
                    "Ya existe un cliente registrado con el RUT indicado.");
        }
        mapRequest(entity, request.clientType(), request.businessName(), rut, request.description(),
                request.businessActivity(), request.email(), request.phone(), request.address(), request.district(),
                request.city(), request.region(), request.postalCode());
        return toResponse(clientRepository.save(entity));
    }

    @Transactional
    public ClientResponse changeStatus(Long id, EntityStatus status) {
        ClientEntity entity = findById(id);
        if (status == EntityStatus.ACTIVE && clientRepository.existsByRutAndIdNot(entity.getRut(), id)) {
            throw new DomainException("CLIENT_RUT_ALREADY_EXISTS",
                    "Ya existe un cliente registrado con el RUT indicado.");
        }
        entity.setStatus(status);
        return toResponse(clientRepository.save(entity));
    }

    public ClientEntity findActiveEntity(Long id) {
        ClientEntity entity = findById(id);
        if (entity.getStatus() != EntityStatus.ACTIVE) {
            throw new DomainException("CLIENT_INACTIVE", "El cliente indicado se encuentra inactivo.");
        }
        return entity;
    }

    public ClientEntity findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new DomainException("CLIENT_NOT_FOUND", "No se encontro el cliente indicado."));
    }

    private void mapRequest(ClientEntity entity, ClientType clientType, String businessName, String rut,
                            String description, String businessActivity, String email, String phone,
                            String address, String district, String city, String region, String postalCode) {
        entity.setClientType(clientType);
        entity.setBusinessName(clean(businessName));
        entity.setRut(rut);
        entity.setDescription(cleanNullable(description));
        entity.setBusinessActivity(cleanNullable(businessActivity));
        entity.setEmail(normalizeEmail(email));
        entity.setPhone(cleanNullable(phone));
        entity.setAddress(cleanNullable(address));
        entity.setDistrict(cleanNullable(district));
        entity.setCity(cleanNullable(city));
        entity.setRegion(cleanNullable(region));
        entity.setPostalCode(cleanNullable(postalCode));
    }

    private ClientResponse toResponse(ClientEntity entity) {
        return new ClientResponse(entity.getId(), entity.getClientType(), entity.getBusinessName(), entity.getRut(),
                entity.getDescription(), entity.getBusinessActivity(), entity.getEmail(), entity.getPhone(),
                entity.getAddress(), entity.getDistrict(), entity.getCity(), entity.getRegion(),
                entity.getPostalCode(), entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private String normalizeRequiredRut(String rut, String code) {
        if (!RutUtils.isValid(rut)) {
            throw new DomainException(code, "El RUT informado no es valido.");
        }
        return RutUtils.normalize(rut);
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
