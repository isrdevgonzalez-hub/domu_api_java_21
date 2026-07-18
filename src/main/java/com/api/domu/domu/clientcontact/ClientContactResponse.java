package com.api.domu.domu.clientcontact;

import com.api.domu.domu.shared.EntityStatus;
import java.time.LocalDateTime;

public record ClientContactResponse(
        Long id,
        Long clientId,
        Long personId,
        String personFullName,
        String personEmail,
        String personPhone,
        ContactType contactType,
        boolean primaryContact,
        boolean receivesQuotes,
        boolean receivesInvoices,
        String notes,
        EntityStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
