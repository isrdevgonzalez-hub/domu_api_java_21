package com.api.domu.domu.client;

import com.api.domu.domu.shared.ValidRut;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @NotNull ClientType clientType,
        @NotBlank @Size(max = 200) String businessName,
        @NotBlank @ValidRut String rut,
        @Size(max = 500) String description,
        @Size(max = 150) String businessActivity,
        @Email @Size(max = 255) String email,
        @Size(max = 50) String phone,
        @Size(max = 255) String address,
        @Size(max = 120) String district,
        @Size(max = 120) String city,
        @Size(max = 120) String region,
        @Size(max = 20) String postalCode
) {
}
