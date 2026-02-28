package com.kry.triage.controller;

import com.kry.triage.dto.AssessmentRequest;
import com.kry.triage.dto.AssessmentResponse;
import com.kry.triage.dto.BookingRequest;
import com.kry.triage.dto.BookingResponse;
import com.kry.triage.dto.NextAvailabilityResponse;
import com.kry.triage.service.AssessmentService;
import com.kry.triage.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Triage", description = "Triage assessment and booking operations")
public class TriageController {

    private final AssessmentService assessmentService;
    private final BookingService bookingService;

    public TriageController(AssessmentService assessmentService, BookingService bookingService) {
        this.assessmentService = assessmentService;
        this.bookingService = bookingService;
    }

    @Operation(summary = "Assess patient score", description = "Returns care recommendation and available appointment slots")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assessment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid score")
    })
    @PostMapping("/assessment")
    public ResponseEntity<AssessmentResponse> assess(@Valid @RequestBody AssessmentRequest request) {
        return ResponseEntity.ok(assessmentService.assess(request.score()));
    }

    @Operation(summary = "Get next available slot", description = "Returns the earliest currently available appointment slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Next availability returned successfully")
    })
    @GetMapping("/availability")
    public ResponseEntity<NextAvailabilityResponse> nextAvailability() {
        return ResponseEntity.ok(assessmentService.getNextAvailability());
    }

    @Operation(summary = "Create booking", description = "Books a selected slot and returns confirmation details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Selected slot is no longer available")
    })
    @PostMapping("/booking")
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }
}
