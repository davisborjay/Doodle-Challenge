package com.doodle.backend.service;

import com.doodle.backend.dto.BookingRequestDto;
import com.doodle.backend.dto.BookingResponseDto;
import reactor.core.publisher.Mono;

public interface BookingService {
    Mono<BookingResponseDto> createBooking(BookingRequestDto request, String idempotencyKey);
    Mono<BookingResponseDto> getBookingById(Long id);
    Mono<Void> deleteBooking(Long id);
}
