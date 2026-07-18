package com.api.domu.domu.person;

import com.api.domu.domu.shared.EntityStatus;
import java.time.LocalDateTime;

public record PersonResponse(
        Long id,
        String firstName,
        String paternalSurname,
        String maternalSurname,
        String rut,
        String email,
        String phone,
        String positionName,
        PersonOrigin origin,
        EntityStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
