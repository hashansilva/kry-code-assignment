package com.kry.triage.service;

import com.kry.triage.dto.AssessmentResponse;
import com.kry.triage.entity.BookingEntity;
import com.kry.triage.model.Recommendation;
import com.kry.triage.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AssessmentService {

    private final RecommendationService recommendationService;
    private final SlotService slotService;
    private final BookingRepository bookingRepository;

    public AssessmentService(
            RecommendationService recommendationService,
            SlotService slotService,
            BookingRepository bookingRepository
    ) {
        this.recommendationService = recommendationService;
        this.slotService = slotService;
        this.bookingRepository = bookingRepository;
    }

    public AssessmentResponse assess(int score) {
        LocalDateTime now = LocalDateTime.now();
        Recommendation recommendation = recommendationService.calculate(score);

        LocalDateTime windowStart = now.withSecond(0).withNano(0);
        LocalDateTime windowEnd = LocalDateTime.of(now.toLocalDate().plusDays(3), LocalTime.of(18, 0));

        List<BookingEntity> bookings = bookingRepository.findBySlotBetween(windowStart, windowEnd);
        List<LocalDateTime> bookedSlots = bookings.stream().map(BookingEntity::getSlot).toList();

        List<LocalDateTime> availableSlots = slotService.getAvailableSlots(now, bookedSlots);
        return new AssessmentResponse(recommendation.toApiValue(), availableSlots);
    }
}
