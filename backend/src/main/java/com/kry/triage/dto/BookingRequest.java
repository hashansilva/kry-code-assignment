package com.kry.triage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(
        @NotNull LocalDateTime slot,
        @NotBlank String recommendation
) {
}
