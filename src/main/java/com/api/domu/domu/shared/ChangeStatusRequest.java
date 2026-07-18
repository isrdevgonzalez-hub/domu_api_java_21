package com.api.domu.domu.shared;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull EntityStatus status
) {
}
