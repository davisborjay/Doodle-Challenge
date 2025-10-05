package com.doodle.backend.controller;

import com.doodle.backend.dto.BookingRequestDto;
import com.doodle.backend.dto.BookingResponseDto;
import com.doodle.backend.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@WebFluxTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDto request;
    private BookingResponseDto response;

    @BeforeEach
    void setup() {
        request = BookingRequestDto.builder()
                .title("Team Meeting")
                .description("Sprint review")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .participantIds(List.of(1L, 2L))
                .slotIds(List.of(1L))
                .build();

        response = BookingResponseDto.builder()
                .id(1L)
                .title("Team Meeting")
                .description("Sprint review")
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .participantIds(List.of(1L, 2L))
                .build();
    }

    @Test
    void createBooking_shouldReturnCreated() {
        Mockito.when(bookingService.createBooking(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/bookings")
                .header("Idempotency-Key", "test-key")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Team Meeting")
                .jsonPath("$.participantIds.length()").isEqualTo(2);
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        Mockito.when(bookingService.getBookingById(1L)).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/bookings/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Team Meeting");
    }

    @Test
    void deleteBooking_shouldReturnNoContent() {
        Mockito.when(bookingService.deleteBooking(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/bookings/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
