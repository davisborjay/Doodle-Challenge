package com.doodle.backend.repository;

import com.doodle.backend.entity.Meeting;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface BookingRepository extends ReactiveCrudRepository<Meeting, Long> {
    @Query("""
    SELECT * FROM meeting
    WHERE idempotency_key = :idempotencyKey
    LIMIT 1
    """)
    Mono<Meeting> findByIdempotencyKey(String idempotencyKey);
}