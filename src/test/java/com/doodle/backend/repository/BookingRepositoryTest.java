package com.doodle.backend.repository;

import com.doodle.backend.entity.Meeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@DataR2dbcTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll().block();
    }

    @Test
    void shouldFindByIdempotencyKey() {
        Meeting meeting = Meeting.builder()
                .title("Test Meeting")
                .description("desc")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .idempotencyKey("abc123")
                .build();

        bookingRepository.save(meeting).block();

        StepVerifier.create(bookingRepository.findByIdempotencyKey("abc123"))
                .expectNextMatches(m -> m.getTitle().equals("Test Meeting"))
                .verifyComplete();
    }
}