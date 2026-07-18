package com.api.domu.domu.client;

import com.api.domu.domu.shared.EntityStatus;
import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        ClientType clientType,
        String businessName,
        String rut,
        String description,
        String businessActivity,
        String email,
        String phone,
        String address,
        String district,
        String city,
        String region,
        String postalCode,
        EntityStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
