package com.api.domu.domu.clientcontact;

import com.api.domu.domu.shared.ApiPaths;
import com.api.domu.domu.shared.ChangeStatusRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/clients/{clientId}/contacts")
public class ClientContactController {

    private final ClientContactService clientContactService;

    public ClientContactController(ClientContactService clientContactService) {
        this.clientContactService = clientContactService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientContactResponse associate(@PathVariable Long clientId,
                                           @Valid @RequestBody AssociateClientContactRequest request) {
        return clientContactService.associate(clientId, request);
    }

    @GetMapping
    public List<ClientContactResponse> list(@PathVariable Long clientId) {
        return clientContactService.listByClient(clientId);
    }

    @GetMapping("/{contactId}")
    public ClientContactResponse getById(@PathVariable Long clientId, @PathVariable Long contactId) {
        return clientContactService.getById(clientId, contactId);
    }

    @PutMapping("/{contactId}")
    public ClientContactResponse update(@PathVariable Long clientId, @PathVariable Long contactId,
                                        @Valid @RequestBody UpdateClientContactRequest request) {
        return clientContactService.update(clientId, contactId, request);
    }

    @PatchMapping("/{contactId}/status")
    public ClientContactResponse changeStatus(@PathVariable Long clientId, @PathVariable Long contactId,
                                              @Valid @RequestBody ChangeStatusRequest request) {
        return clientContactService.changeStatus(clientId, contactId, request.status());
    }

    @PatchMapping("/{contactId}/primary")
    public ClientContactResponse setPrimary(@PathVariable Long clientId, @PathVariable Long contactId) {
        return clientContactService.setPrimary(clientId, contactId);
    }
}
