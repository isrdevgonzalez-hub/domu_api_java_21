package com.api.domu.domu.shared;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        LocalDateTime timestamp,
        Map<String, String> validationErrors
) {
}
