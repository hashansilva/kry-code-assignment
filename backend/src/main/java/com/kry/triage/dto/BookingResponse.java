package com.kry.triage.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        String confirmationId,
        LocalDateTime slot,
        String recommendation
) {
}
