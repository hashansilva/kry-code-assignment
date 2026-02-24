package com.kry.triage.controller;

import com.kry.triage.dto.AssessmentRequest;
import com.kry.triage.dto.AssessmentResponse;
import com.kry.triage.dto.BookingRequest;
import com.kry.triage.dto.BookingResponse;
import com.kry.triage.service.AssessmentService;
import com.kry.triage.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TriageController {

    private final AssessmentService assessmentService;
    private final BookingService bookingService;

    public TriageController(AssessmentService assessmentService, BookingService bookingService) {
        this.assessmentService = assessmentService;
        this.bookingService = bookingService;
    }

    @PostMapping("/assessment")
    public ResponseEntity<AssessmentResponse> assess(@Valid @RequestBody AssessmentRequest request) {
        return ResponseEntity.ok(assessmentService.assess(request.score()));
    }

    @PostMapping("/booking")
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }
}
