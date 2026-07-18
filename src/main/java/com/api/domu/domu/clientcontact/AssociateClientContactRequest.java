package com.api.domu.domu.clientcontact;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssociateClientContactRequest(
        @NotNull Long personId,
        @NotNull ContactType contactType,
        boolean primaryContact,
        boolean receivesQuotes,
        boolean receivesInvoices,
        @Size(max = 500) String notes
) {
}
