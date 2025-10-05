package com.doodle.backend.repository;

import com.doodle.backend.entity.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@DataR2dbcTest
class SlotRepositoryTest {

    @Autowired
    private SlotRepository slotRepository;

    private TimeSlot slot1;
    private TimeSlot slot2;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        slot1 = TimeSlot.builder()
                .userId(1L)
                .startTime(now)
                .endTime(now.plusHours(1))
                .busy(false)
                .idempotencyKey("key-1")
                .build();

        slot2 = TimeSlot.builder()
                .userId(2L)
                .startTime(now.plusHours(2))
                .endTime(now.plusHours(3))
                .busy(false)
                .idempotencyKey("key-2")
                .build();

        slotRepository.deleteAll()
                .thenMany(slotRepository.saveAll(
                        java.util.List.of(slot1, slot2)))
                .blockLast();
    }

    @Test
    void shouldFindOverlappingSlot() {
        LocalDateTime start = slot1.getStartTime().plusMinutes(30);
        LocalDateTime end = slot1.getEndTime().plusMinutes(30);

        StepVerifier.create(slotRepository.findOverlappingSlot(start, end))
                .expectNextMatches(s -> s.getIdempotencyKey().equals("key-1"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoOverlap() {
        LocalDateTime start = slot2.getEndTime().plusMinutes(10);
        LocalDateTime end = start.plusHours(1);

        StepVerifier.create(slotRepository.findOverlappingSlot(start, end))
                .verifyComplete();
    }

    @Test
    void shouldFindByUserId() {
        StepVerifier.create(slotRepository.findByUserId(1L))
                .expectNextMatches(s -> s.getUserId().equals(1L))
                .verifyComplete();
    }
}