package com.doodle.backend.mapper;

import com.doodle.backend.dto.BookingRequestDto;
import com.doodle.backend.dto.BookingResponseDto;
import com.doodle.backend.entity.Meeting;
import com.doodle.backend.entity.MeetingParticipant;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public static BookingResponseDto buildBookingResponseDto(BookingRequestDto request, Meeting savedMeeting) {
        return BookingResponseDto.builder()
                .id(savedMeeting.getId())
                .title(savedMeeting.getTitle())
                .description(savedMeeting.getDescription())
                .startTime(savedMeeting.getStartTime())
                .endTime(savedMeeting.getEndTime())
                .participantIds(request.getParticipantIds())
                .build();
    }

    public static MeetingParticipant buildMeetingParticipant(Meeting savedMeeting, Long userId) {
        return MeetingParticipant.builder()
                .meetingId(savedMeeting.getId())
                .userId(userId)
                .build();
    }

    public static Meeting buildMeting(BookingRequestDto request, String idempotencyKey) {
        return Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .idempotencyKey(idempotencyKey)
                .build();
    }
}
