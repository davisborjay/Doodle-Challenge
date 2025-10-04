package com.doodle.backend.repository;

import com.doodle.backend.entity.MeetingParticipant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MeetingParticipantRepository extends ReactiveCrudRepository<MeetingParticipant, Long> {
    Flux<MeetingParticipant> findByMeetingId(Long meetingId);
    Mono<Void> deleteByMeetingId(Long meetingId);
}