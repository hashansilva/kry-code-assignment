package com.kry.triage.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum Recommendation {
    CHAT,
    NURSE,
    DOCTOR;

    public String toApiValue() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static Recommendation fromApiValue(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recommendation is required");
        }

        return switch (value.trim().toLowerCase()) {
            case "chat" -> CHAT;
            case "nurse" -> NURSE;
            case "doctor" -> DOCTOR;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid recommendation");
        };
    }
}
