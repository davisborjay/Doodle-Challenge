package com.doodle.backend.controller;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@WebFluxTest(SlotController.class)
class SlotControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SlotService slotService;

    private SlotRequestDto request;
    private SlotResponseDto response;

    @BeforeEach
    void setup() {
        request = SlotRequestDto.builder()
                .userId(1L)
                .startTime(LocalDateTime.now().plusDays(1)).
                endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();

        response = SlotResponseDto.builder()
                .id(1L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .reserved(false)
                .build();
    }

    @Test
    void createSlot_shouldReturnCreated() {
        Mockito.when(slotService.createSlot(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/slots")
                .header("Idempotency-Key", "slot-key")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void getSlotsByUser_shouldReturnFlux() {
        Mockito.when(slotService.getSlotsByUser(1L))
                .thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/api/v1/slots/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SlotResponseDto.class)
                .hasSize(1);
    }

    @Test
    void deleteSlot_shouldReturnNoContent() {
        Mockito.when(slotService.deleteSlotById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/slots/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateSlot_shouldReturnUpdated() {
        Mockito.when(slotService.updateSlot(Mockito.eq(1L), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/v1/slots/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }
}