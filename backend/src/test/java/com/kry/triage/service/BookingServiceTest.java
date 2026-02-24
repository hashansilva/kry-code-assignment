package com.kry.triage.service;

import com.kry.triage.dto.BookingRequest;
import com.kry.triage.dto.BookingResponse;
import com.kry.triage.entity.BookingEntity;
import com.kry.triage.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SlotService slotService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void shouldCreateBookingWhenSlotIsAvailable() {
        LocalDateTime requested = LocalDateTime.of(2026, 2, 20, 9, 15, 45);
        LocalDateTime normalized = LocalDateTime.of(2026, 2, 20, 9, 15);
        BookingRequest request = new BookingRequest(requested, "nurse");

        when(bookingRepository.countBySlot(normalized)).thenReturn(1L);
        when(slotService.isBookable(any(LocalDateTime.class), eq(normalized), eq(1L))).thenReturn(true);
        when(bookingRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response.confirmationId());
        assertFalse(response.confirmationId().isBlank());
        assertEquals(normalized, response.slot());
        assertEquals("Nurse", response.recommendation());

        ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(normalized, captor.getValue().getSlot());
        assertEquals("Nurse", captor.getValue().getRecommendation());
    }

    @Test
    void shouldThrowConflictWhenSlotIsNotAvailable() {
        LocalDateTime slot = LocalDateTime.of(2026, 2, 20, 9, 15);
        BookingRequest request = new BookingRequest(slot, "Doctor");

        when(bookingRepository.countBySlot(slot)).thenReturn(4L);
        when(slotService.isBookable(any(LocalDateTime.class), eq(slot), eq(4L))).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.createBooking(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }

    @Test
    void shouldRejectInvalidRecommendation() {
        BookingRequest request = new BookingRequest(LocalDateTime.of(2026, 2, 20, 9, 15), "invalid");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.createBooking(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(bookingRepository, never()).countBySlot(any(LocalDateTime.class));
        verify(bookingRepository, never()).save(any(BookingEntity.class));
    }
}
