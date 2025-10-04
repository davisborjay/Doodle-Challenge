package com.doodle.backend.service.impl;

import com.doodle.backend.dto.BookingRequestDto;
import com.doodle.backend.dto.BookingResponseDto;
import com.doodle.backend.entity.Meeting;
import com.doodle.backend.entity.MeetingParticipant;
import com.doodle.backend.repository.BookingRepository;
import com.doodle.backend.repository.MeetingParticipantRepository;
import com.doodle.backend.repository.SlotRepository;
import com.doodle.backend.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SlotRepository slotRepository;
    @Autowired
    private MeetingParticipantRepository participantRepository;

    @Override
    public Mono<BookingResponseDto> createBooking(BookingRequestDto request, String idempotencyKey) {
        log.info("calling bookingRepository.createBooking for title={}", request.getTitle());

        return bookingRepository.findByIdempotencyKey(idempotencyKey)
                .flatMap(existing -> {
                    log.info("Idempotent request detected for key={}", idempotencyKey);
                    return Mono.just(buildBookingResponseDto(request, existing));
                })
                .switchIfEmpty(
                        // validate slots before creating the meeting
                        Flux.fromIterable(request.getSlotIds())
                                .flatMap(slotId ->
                                        slotRepository.findById(slotId)
                                                .switchIfEmpty(Mono.error(
                                                        new IllegalArgumentException("Slot with ID " + slotId + " not found")))
                                                .flatMap(slot -> {
                                                    if (slot.isBusy()) {
                                                        return Mono.error(new IllegalArgumentException("Slot with ID " + slotId + " is already busy"));
                                                    }
                                                    return Mono.just(slot);
                                                })
                                )
                                .collectList()
                                // Create the meeting if all slots are valid
                                .flatMap(validSlots ->
                                        bookingRepository.save(buildMeting(request, idempotencyKey))
                                                .flatMap(savedMeeting ->
                                                        Flux.fromIterable(validSlots)
                                                                .flatMap(slot -> {
                                                                    slot.setBusy(true);
                                                                    slot.setMeetingId(savedMeeting.getId());
                                                                    return slotRepository.save(slot);
                                                                })
                                                                .thenMany(
                                                                        Flux.fromIterable(request.getParticipantIds())
                                                                                .flatMap(userId -> participantRepository.save(
                                                                                        buildMeetingParticipant(savedMeeting, userId)))
                                                                )
                                                                .collectList()
                                                                .then(Mono.just(buildBookingResponseDto(request, savedMeeting)))
                                                )
                                )
                );
    }

    private static BookingResponseDto buildBookingResponseDto(BookingRequestDto request, Meeting savedMeeting) {
        return BookingResponseDto.builder()
                .id(savedMeeting.getId())
                .title(savedMeeting.getTitle())
                .description(savedMeeting.getDescription())
                .startTime(savedMeeting.getStartTime())
                .endTime(savedMeeting.getEndTime())
                .participantIds(request.getParticipantIds())
                .build();
    }

    private static MeetingParticipant buildMeetingParticipant(Meeting savedMeeting, Long userId) {
        return MeetingParticipant.builder()
                .meetingId(savedMeeting.getId())
                .userId(userId)
                .build();
    }

    private static Meeting buildMeting(BookingRequestDto request, String idempotencyKey) {
        return Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .idempotencyKey(idempotencyKey)
                .build();
    }

    @Override
    public Mono<BookingResponseDto> getBookingById(Long id) {
        return bookingRepository.findById(id)
                .flatMap(meeting ->
                        participantRepository.findByMeetingId(id)
                                .map(MeetingParticipant::getUserId)
                                .collectList()
                                .map(userIds -> BookingResponseDto
                                        .builder()
                                        .title(meeting.getTitle())
                                        .participantIds(userIds)
                                        .description(meeting.getDescription())
                                        .endTime(meeting.getEndTime())
                                        .startTime(meeting.getStartTime())
                                        .id(meeting.getId())
                                        .build())
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Meeting not found")));
    }

    @Override
    public Mono<Void> deleteBooking(Long id) {
        log.info("Deleting booking {}", id);
        return participantRepository.deleteByMeetingId(id)
                .then(slotRepository.findByMeetingId(id)
                        .flatMap(slot -> {
                            slot.setBusy(false);
                            slot.setMeetingId(null);
                            return slotRepository.save(slot);
                        })
                )
                .then(bookingRepository.deleteById(id));
    }
}
