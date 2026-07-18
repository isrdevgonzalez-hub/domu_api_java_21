package com.api.domu.domu.person;

import com.api.domu.domu.shared.ValidRut;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePersonRequest(
        @NotBlank @Size(max = 120) String firstName,
        @NotBlank @Size(max = 120) String paternalSurname,
        @Size(max = 120) String maternalSurname,
        @ValidRut(required = false) String rut,
        @Email @Size(max = 255) String email,
        @Size(max = 50) String phone,
        @Size(max = 120) String positionName,
        @NotNull PersonOrigin origin
) {
}
