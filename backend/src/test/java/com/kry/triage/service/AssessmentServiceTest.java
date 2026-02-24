package com.kry.triage.service;

import com.kry.triage.dto.AssessmentResponse;
import com.kry.triage.entity.BookingEntity;
import com.kry.triage.model.Recommendation;
import com.kry.triage.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private SlotService slotService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    @Test
    void shouldReturnRecommendationAndAvailableSlots() {
        BookingEntity booking1 = new BookingEntity();
        booking1.setSlot(LocalDateTime.of(2026, 2, 20, 9, 0));

        BookingEntity booking2 = new BookingEntity();
        booking2.setSlot(LocalDateTime.of(2026, 2, 20, 9, 15));

        List<LocalDateTime> bookedSlots = List.of(booking1.getSlot(), booking2.getSlot());
        List<LocalDateTime> availableSlots = List.of(
                LocalDateTime.of(2026, 2, 20, 10, 0),
                LocalDateTime.of(2026, 2, 20, 10, 15)
        );

        when(recommendationService.calculate(10)).thenReturn(Recommendation.NURSE);
        when(bookingRepository.findBySlotBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking1, booking2));
        when(slotService.getAvailableSlots(any(LocalDateTime.class), eq(bookedSlots))).thenReturn(availableSlots);

        AssessmentResponse response = assessmentService.assess(10);

        assertEquals("Nurse", response.recommendation());
        assertEquals(availableSlots, response.availableSlots());

        verify(recommendationService).calculate(10);
        verify(bookingRepository).findBySlotBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(slotService).getAvailableSlots(any(LocalDateTime.class), eq(bookedSlots));
    }
}
