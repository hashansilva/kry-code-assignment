package com.kry.triage.repository;

import com.kry.triage.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findBySlotBetween(LocalDateTime start, LocalDateTime end);

    long countBySlot(LocalDateTime slot);
}
