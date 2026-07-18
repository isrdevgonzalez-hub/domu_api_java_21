package com.api.domu.domu.shared;

import java.util.Locale;

public final class RutUtils {

    private RutUtils() {
    }

    public static String normalize(String rut) {
        if (rut == null) {
            return null;
        }
        String sanitized = rut.trim().replace(".", "").replace("-", "").toUpperCase(Locale.ROOT);
        if (sanitized.isBlank()) {
            return null;
        }
        if (sanitized.length() < 2) {
            throw new IllegalArgumentException("RUT must contain body and verifier digit");
        }
        return sanitized.substring(0, sanitized.length() - 1) + "-" + sanitized.charAt(sanitized.length() - 1);
    }

    public static boolean isValid(String rut) {
        if (rut == null || rut.trim().isBlank()) {
            return false;
        }
        try {
            String normalized = normalize(rut);
            String[] parts = normalized.split("-");
            if (parts.length != 2 || !parts[0].chars().allMatch(Character::isDigit)) {
                return false;
            }
            int sum = 0;
            int multiplier = 2;
            for (int i = parts[0].length() - 1; i >= 0; i--) {
                sum += Character.getNumericValue(parts[0].charAt(i)) * multiplier;
                multiplier = multiplier == 7 ? 2 : multiplier + 1;
            }
            int result = 11 - (sum % 11);
            String expected = switch (result) {
                case 11 -> "0";
                case 10 -> "K";
                default -> String.valueOf(result);
            };
            return expected.equals(parts[1]);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
