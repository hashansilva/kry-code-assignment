package com.kry.triage.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotServiceTest {

    private final SlotService slotService = new SlotService();

    @Test
    void shouldReturnFutureSlotsInsideThreeDayWindow() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 20, 7, 53);

        List<LocalDateTime> slots = slotService.getAvailableSlots(now, List.of());

        assertFalse(slots.isEmpty());
        assertEquals(LocalDateTime.of(2026, 2, 20, 8, 0), slots.get(0));
        assertTrue(slots.stream().allMatch(slot -> !slot.isBefore(LocalDateTime.of(2026, 2, 20, 8, 0))));
        assertTrue(slots.stream().allMatch(slot -> !slot.toLocalDate().isAfter(LocalDate.of(2026, 2, 23))));
    }

    @Test
    void shouldRespectBreaksAndShiftAvailability() {
        assertEquals(3, slotService.availableClinicianCount(LocalDateTime.of(2026, 2, 20, 12, 0)));
        assertEquals(2, slotService.availableClinicianCount(LocalDateTime.of(2026, 2, 20, 17, 45)));
        assertEquals(0, slotService.availableClinicianCount(LocalDateTime.of(2026, 2, 20, 18, 0)));
    }

    @Test
    void shouldConsiderExistingBookingsWhenBookabilityIsChecked() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 20, 9, 0);
        LocalDateTime slot = LocalDateTime.of(2026, 2, 20, 10, 0);

        assertTrue(slotService.isBookable(now, slot, 0));
        assertFalse(slotService.isBookable(now, slot, 4));
    }
}
