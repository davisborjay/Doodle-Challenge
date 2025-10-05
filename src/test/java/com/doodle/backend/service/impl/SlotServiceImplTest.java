package com.doodle.backend.service.impl;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.entity.TimeSlot;
import com.doodle.backend.repository.SlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SlotServiceImplTest {

    @Mock
    private SlotRepository slotRepository;

    @InjectMocks
    private SlotServiceImpl slotService;

    private SlotRequestDto request;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = SlotRequestDto.builder()
                .userId(1L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        timeSlot = TimeSlot.builder()
                .id(1L)
                .userId(1L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .busy(false)
                .idempotencyKey("123")
                .build();
    }

    @Test
    void createSlot_shouldReturnExistingSlot_whenIdempotencyKeyExists() {
        when(slotRepository.findByIdempotencyKey(anyString())).thenReturn(Mono.just(timeSlot));
        when(slotRepository.findOverlappingSlot(any(), any())).thenReturn(Mono.empty());

        Mono<SlotResponseDto> result = slotService.createSlot(request, "123");

        StepVerifier.create(result)
                .expectNextMatches(slot -> slot.getId().equals(1L))
                .verifyComplete();

        verify(slotRepository, never()).save(any());
    }

    @Test
    void createSlot_shouldSaveNewSlot_whenNoOverlap() {
        when(slotRepository.findByIdempotencyKey(anyString())).thenReturn(Mono.empty());
        when(slotRepository.findOverlappingSlot(any(), any())).thenReturn(Mono.empty());
        when(slotRepository.save(any(TimeSlot.class))).thenReturn(Mono.just(timeSlot));

        Mono<SlotResponseDto> result = slotService.createSlot(request, "key-123");

        StepVerifier.create(result)
                .expectNextMatches(slot -> slot.getId() == 1L && !slot.isReserved())
                .verifyComplete();

        verify(slotRepository).save(any());
    }

    @Test
    void getSlotsByUser_shouldReturnSlots() {
        when(slotRepository.findByUserId(1L)).thenReturn(Flux.just(timeSlot));

        StepVerifier.create(slotService.getSlotsByUser(1L))
                .expectNextMatches(slot -> slot.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void updateSlot_shouldUpdateSuccessfully() {
        when(slotRepository.findById(1L)).thenReturn(Mono.just(timeSlot));
        when(slotRepository.save(any(TimeSlot.class))).thenReturn(Mono.just(timeSlot));

        StepVerifier.create(slotService.updateSlot(1L, request))
                .expectNextMatches(slot -> slot.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void updateSlot_shouldThrowError_whenSlotNotFound() {
        when(slotRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(slotService.updateSlot(1L, request))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().equals("Slot not found"))
                .verify();
    }
}