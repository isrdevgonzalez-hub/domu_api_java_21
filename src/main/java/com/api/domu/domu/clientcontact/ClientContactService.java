package com.api.domu.domu.clientcontact;

import com.api.domu.domu.client.ClientEntity;
import com.api.domu.domu.client.ClientService;
import com.api.domu.domu.person.PersonEntity;
import com.api.domu.domu.person.PersonService;
import com.api.domu.domu.shared.DomainException;
import com.api.domu.domu.shared.EntityStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientContactService {

    private final ClientContactRepository clientContactRepository;
    private final ClientService clientService;
    private final PersonService personService;

    public ClientContactService(ClientContactRepository clientContactRepository, ClientService clientService,
                                PersonService personService) {
        this.clientContactRepository = clientContactRepository;
        this.clientService = clientService;
        this.personService = personService;
    }

    @Transactional
    public ClientContactResponse associate(Long clientId, AssociateClientContactRequest request) {
        ClientEntity client = clientService.findActiveEntity(clientId);
        PersonEntity person = personService.findActiveEntity(request.personId());
        if (!hasContactInfo(person)) {
            throw new DomainException("CONTACT_EMAIL_REQUIRED",
                    "La persona debe tener email o telefono para ser asociada como contacto.");
        }
        if (clientContactRepository.existsByClientIdAndPersonId(clientId, request.personId())) {
            throw new DomainException("CONTACT_ALREADY_ASSOCIATED",
                    "La persona ya se encuentra asociada al cliente.");
        }
        validateDeliveryRules(person, request.receivesQuotes(), request.receivesInvoices());
        if (request.primaryContact()) {
            clearCurrentPrimary(clientId);
        }
        ClientContactEntity entity = new ClientContactEntity();
        entity.setClient(client);
        entity.setPerson(person);
        entity.setContactType(request.contactType());
        entity.setPrimaryContact(request.primaryContact());
        entity.setReceivesQuotes(request.receivesQuotes());
        entity.setReceivesInvoices(request.receivesInvoices());
        entity.setNotes(cleanNullable(request.notes()));
        entity.setStatus(EntityStatus.ACTIVE);
        return toResponse(clientContactRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ClientContactResponse> listByClient(Long clientId) {
        clientService.findById(clientId);
        return clientContactRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientContactResponse getById(Long clientId, Long contactId) {
        clientService.findById(clientId);
        return toResponse(findById(clientId, contactId));
    }

    @Transactional
    public ClientContactResponse update(Long clientId, Long contactId, UpdateClientContactRequest request) {
        clientService.findActiveEntity(clientId);
        ClientContactEntity entity = findById(clientId, contactId);
        if (entity.getStatus() != EntityStatus.ACTIVE) {
            throw new DomainException("CLIENT_CONTACT_INACTIVE", "La relacion cliente-contacto esta inactiva.");
        }
        PersonEntity person = personService.findActiveEntity(entity.getPerson().getId());
        validateDeliveryRules(person, request.receivesQuotes(), request.receivesInvoices());
        if (request.primaryContact()) {
            clearCurrentPrimary(clientId);
        }
        entity.setContactType(request.contactType());
        entity.setPrimaryContact(request.primaryContact());
        entity.setReceivesQuotes(request.receivesQuotes());
        entity.setReceivesInvoices(request.receivesInvoices());
        entity.setNotes(cleanNullable(request.notes()));
        return toResponse(clientContactRepository.save(entity));
    }

    @Transactional
    public ClientContactResponse changeStatus(Long clientId, Long contactId, EntityStatus status) {
        ClientContactEntity entity = findById(clientId, contactId);
        if (status == EntityStatus.ACTIVE) {
            clientService.findActiveEntity(clientId);
            PersonEntity person = personService.findActiveEntity(entity.getPerson().getId());
            validateDeliveryRules(person, entity.isReceivesQuotes(), entity.isReceivesInvoices());
        }
        entity.setStatus(status);
        if (status == EntityStatus.INACTIVE) {
            entity.setPrimaryContact(false);
        }
        return toResponse(clientContactRepository.save(entity));
    }

    @Transactional
    public ClientContactResponse setPrimary(Long clientId, Long contactId) {
        clientService.findActiveEntity(clientId);
        ClientContactEntity entity = findById(clientId, contactId);
        if (entity.getStatus() != EntityStatus.ACTIVE) {
            throw new DomainException("CLIENT_CONTACT_INACTIVE", "La relacion cliente-contacto esta inactiva.");
        }
        personService.findActiveEntity(entity.getPerson().getId());
        clearCurrentPrimary(clientId);
        entity.setPrimaryContact(true);
        return toResponse(clientContactRepository.save(entity));
    }

    private void clearCurrentPrimary(Long clientId) {
        clientContactRepository.findByClientIdAndPrimaryContactTrueAndStatus(clientId, EntityStatus.ACTIVE)
                .ifPresent(current -> {
                    current.setPrimaryContact(false);
                    clientContactRepository.save(current);
                });
    }

    private ClientContactEntity findById(Long clientId, Long contactId) {
        return clientContactRepository.findByIdAndClientId(contactId, clientId)
                .orElseThrow(() -> new DomainException("CONTACT_NOT_FOUND",
                        "No se encontro el contacto asociado al cliente indicado."));
    }

    private void validateDeliveryRules(PersonEntity person, boolean receivesQuotes, boolean receivesInvoices) {
        if ((receivesQuotes || receivesInvoices) && (person.getEmail() == null || person.getEmail().isBlank())) {
            throw new DomainException("CONTACT_EMAIL_REQUIRED",
                    "La persona debe tener email valido para recibir cotizaciones o facturas.");
        }
    }

    private boolean hasContactInfo(PersonEntity person) {
        return (person.getEmail() != null && !person.getEmail().isBlank())
                || (person.getPhone() != null && !person.getPhone().isBlank());
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }

    private ClientContactResponse toResponse(ClientContactEntity entity) {
        String fullName = String.join(" ",
                entity.getPerson().getFirstName(),
                entity.getPerson().getPaternalSurname(),
                entity.getPerson().getMaternalSurname() == null ? "" : entity.getPerson().getMaternalSurname())
                .trim();
        return new ClientContactResponse(entity.getId(), entity.getClient().getId(), entity.getPerson().getId(),
                fullName, entity.getPerson().getEmail(), entity.getPerson().getPhone(), entity.getContactType(),
                entity.isPrimaryContact(), entity.isReceivesQuotes(), entity.isReceivesInvoices(), entity.getNotes(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
