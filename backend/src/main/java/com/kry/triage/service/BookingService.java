package com.kry.triage.service;

import com.kry.triage.dto.BookingRequest;
import com.kry.triage.dto.BookingResponse;
import com.kry.triage.entity.BookingEntity;
import com.kry.triage.model.Recommendation;
import com.kry.triage.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotService slotService;

    public BookingService(BookingRepository bookingRepository, SlotService slotService) {
        this.bookingRepository = bookingRepository;
        this.slotService = slotService;
    }

    public BookingResponse createBooking(BookingRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime normalizedSlot = request.slot().withSecond(0).withNano(0);

        Recommendation recommendation = Recommendation.fromApiValue(request.recommendation());
        long existingBookings = bookingRepository.countBySlot(normalizedSlot);

        if (!slotService.isBookable(now, normalizedSlot, existingBookings)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "selected slot is no longer available");
        }

        BookingEntity entity = new BookingEntity();
        entity.setConfirmationId(UUID.randomUUID().toString());
        entity.setSlot(normalizedSlot);
        entity.setRecommendation(recommendation.toApiValue());
        entity.setCreatedAt(now);

        BookingEntity saved = bookingRepository.save(entity);
        return new BookingResponse(saved.getConfirmationId(), saved.getSlot(), saved.getRecommendation());
    }
}
