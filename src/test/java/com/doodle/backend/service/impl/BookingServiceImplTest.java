package com.doodle.backend.service.impl;

import com.doodle.backend.dto.BookingRequestDto;
import com.doodle.backend.dto.BookingResponseDto;
import com.doodle.backend.entity.Meeting;
import com.doodle.backend.entity.MeetingParticipant;
import com.doodle.backend.entity.TimeSlot;
import com.doodle.backend.repository.BookingRepository;
import com.doodle.backend.repository.MeetingParticipantRepository;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private SlotRepository slotRepository;
    @Mock
    private MeetingParticipantRepository participantRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequestDto request;
    private Meeting meeting;
    private TimeSlot slot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = BookingRequestDto.builder()
                .title("Team Sync")
                .description("Weekly meeting")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .slotIds(List.of(1L))
                .participantIds(List.of(10L, 20L))
                .build();

        meeting = Meeting.builder()
                .id(1L)
                .title("Team Sync")
                .description("Weekly meeting")
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .idempotencyKey("abc")
                .build();

        slot = TimeSlot.builder()
                .id(1L)
                .userId(1L)
                .busy(false)
                .build();
    }

    @Test
    void createBooking_shouldReturnExistingBooking_whenIdempotent() {
        when(bookingRepository.findByIdempotencyKey("abc")).thenReturn(Mono.just(meeting));

        StepVerifier.create(bookingService.createBooking(request, "abc"))
                .expectNextMatches(response -> response.getId().equals(1L))
                .verifyComplete();

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldCreateNewBookingSuccessfully() {
        when(bookingRepository.findByIdempotencyKey(anyString())).thenReturn(Mono.empty());
        when(slotRepository.findById(1L)).thenReturn(Mono.just(slot));
        when(bookingRepository.save(any(Meeting.class))).thenReturn(Mono.just(meeting));
        when(slotRepository.save(any(TimeSlot.class))).thenReturn(Mono.just(slot));
        when(participantRepository.save(any(MeetingParticipant.class))).thenReturn(Mono.just(
                MeetingParticipant.builder().meetingId(1L).userId(10L).build()
        ));

        StepVerifier.create(bookingService.createBooking(request, "new-key"))
                .expectNextMatches(resp -> resp.getTitle().equals("Team Sync"))
                .verifyComplete();
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Mono.just(meeting));
        when(participantRepository.findByMeetingId(1L))
                .thenReturn(Flux.just(MeetingParticipant.builder().meetingId(1L).userId(10L).build()));

        StepVerifier.create(bookingService.getBookingById(1L))
                .expectNextMatches(b -> b.getTitle().equals("Team Sync"))
                .verifyComplete();
    }

    @Test
    void getBookingById_shouldThrowError_whenNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getBookingById(1L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().equals("Meeting not found"))
                .verify();
    }

    @Test
    void deleteBooking_shouldExecuteSuccessfully() {
        when(participantRepository.deleteByMeetingId(1L)).thenReturn(Mono.empty());
        when(slotRepository.findByMeetingId(1L)).thenReturn(Mono.just(slot));
        when(slotRepository.save(any(TimeSlot.class))).thenReturn(Mono.just(slot));
        when(bookingRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.deleteBooking(1L))
                .verifyComplete();
    }
}
