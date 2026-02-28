package com.kry.triage.dto;

import java.time.LocalDateTime;

public record NextAvailabilityResponse(LocalDateTime nextAvailableSlot) {
}
