package com.kry.triage.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SlotService {

    private static final LocalTime CLINIC_OPEN = LocalTime.of(8, 0);
    private static final LocalTime CLINIC_CLOSE = LocalTime.of(18, 0);
    private static final int SLOT_MINUTES = 15;

    // Assumption: clinicians start at staggered times to keep capacity through the day.
    private static final List<LocalTime> CLINICIAN_START_TIMES = List.of(
            LocalTime.of(8, 0),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0)
    );

    public List<LocalDateTime> getAvailableSlots(LocalDateTime requestTime, Collection<LocalDateTime> bookedSlots) {
        LocalDateTime normalizedNow = requestTime.withSecond(0).withNano(0);
        LocalDateTime firstCandidate = roundUpToQuarter(normalizedNow);
        LocalDate endDate = normalizedNow.toLocalDate().plusDays(3);
        LocalDateTime lastCandidate = LocalDateTime.of(endDate, CLINIC_CLOSE).minusMinutes(SLOT_MINUTES);

        Map<LocalDateTime, Integer> bookingCounts = toBookingCountMap(bookedSlots);
        List<LocalDateTime> result = new ArrayList<>();

        for (LocalDateTime slot = firstCandidate; !slot.isAfter(lastCandidate); slot = slot.plusMinutes(SLOT_MINUTES)) {
            if (!isWithinClinicHours(slot)) {
                continue;
            }

            int booked = bookingCounts.getOrDefault(slot, 0);
            if (availableClinicianCount(slot) > booked) {
                result.add(slot);
            }
        }

        return result;
    }

    public boolean isBookable(LocalDateTime now, LocalDateTime slot, long existingBookingsForSlot) {
        LocalDateTime normalizedNow = now.withSecond(0).withNano(0);
        LocalDateTime normalizedSlot = slot.withSecond(0).withNano(0);

        LocalDate maxDate = normalizedNow.toLocalDate().plusDays(3);
        LocalDateTime firstBookable = roundUpToQuarter(normalizedNow);
        LocalDateTime lastBookable = LocalDateTime.of(maxDate, CLINIC_CLOSE).minusMinutes(SLOT_MINUTES);

        if (normalizedSlot.isBefore(firstBookable) || normalizedSlot.isAfter(lastBookable)) {
            return false;
        }

        if (!isWithinClinicHours(normalizedSlot)) {
            return false;
        }

        return availableClinicianCount(normalizedSlot) > existingBookingsForSlot;
    }

    int availableClinicianCount(LocalDateTime slot) {
        if (!isWithinClinicHours(slot)) {
            return 0;
        }

        int available = 0;
        LocalDate day = slot.toLocalDate();

        for (LocalTime startTime : CLINICIAN_START_TIMES) {
            LocalDateTime shiftStart = LocalDateTime.of(day, startTime);
            LocalDateTime shiftEnd = shiftStart.plusHours(8);
            LocalDateTime breakStart = shiftStart.plusHours(4);
            LocalDateTime breakEnd = breakStart.plusHours(1);

            boolean inShift = !slot.isBefore(shiftStart) && slot.isBefore(shiftEnd);
            boolean inBreak = !slot.isBefore(breakStart) && slot.isBefore(breakEnd);

            if (inShift && !inBreak) {
                available++;
            }
        }

        return available;
    }

    private boolean isWithinClinicHours(LocalDateTime slot) {
        LocalTime time = slot.toLocalTime();
        return !time.isBefore(CLINIC_OPEN) && time.isBefore(CLINIC_CLOSE);
    }

    private LocalDateTime roundUpToQuarter(LocalDateTime time) {
        LocalDateTime base = time.withSecond(0).withNano(0);
        int minute = base.getMinute();
        int remainder = minute % SLOT_MINUTES;

        if (remainder == 0) {
            return base;
        }

        return base.plusMinutes(SLOT_MINUTES - remainder);
    }

    private Map<LocalDateTime, Integer> toBookingCountMap(Collection<LocalDateTime> bookedSlots) {
        Map<LocalDateTime, Integer> counts = new HashMap<>();
        for (LocalDateTime slot : bookedSlots) {
            LocalDateTime normalized = slot.withSecond(0).withNano(0);
            counts.put(normalized, counts.getOrDefault(normalized, 0) + 1);
        }
        return counts;
    }
}
