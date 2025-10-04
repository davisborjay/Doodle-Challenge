package com.doodle.backend.controller;

import com.doodle.backend.dto.BookingRequestDto;
import com.doodle.backend.dto.BookingResponseDto;
import com.doodle.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookings")
@Slf4j
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequestDto request,
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {
        log.info("POST /api/v1/bookings with Idempotency-Key={} | Request={}", idempotencyKey, request);
        return bookingService.createBooking(request, idempotencyKey);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BookingResponseDto> getBookingById(@PathVariable Long id) {
        log.info("GET /api/v1/bookings/{}", id);
        return bookingService.getBookingById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBooking(@PathVariable Long id) {
        log.info("DELETE /api/v1/bookings/{}", id);
        return bookingService.deleteBooking(id);
    }
}
