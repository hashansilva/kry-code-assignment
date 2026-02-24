package com.kry.triage.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AssessmentResponse(
        String recommendation,
        List<LocalDateTime> availableSlots
) {
}
