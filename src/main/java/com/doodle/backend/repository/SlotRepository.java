package com.doodle.backend.repository;

import com.doodle.backend.entity.TimeSlot;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SlotRepository extends ReactiveCrudRepository<TimeSlot, Long> {
    @Query("""
    SELECT * FROM time_slot
    WHERE (:startTime < end_time AND :endTime > start_time)
    LIMIT 1
    """)
    Mono<TimeSlot> findOverlappingSlot(LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
    SELECT * FROM time_slot
    WHERE idempotency_key = :idempotencyKey
    LIMIT 1
    """)
    Mono<TimeSlot> findByIdempotencyKey(String idempotencyKey);

    @Query("""
        SELECT * FROM time_slot
        WHERE user_id = :userId
        ORDER BY start_time
    """)
    Flux<TimeSlot> findByUserId(Long userId);
}