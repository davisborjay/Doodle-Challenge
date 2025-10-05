package com.doodle.backend.mapper;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.entity.TimeSlot;
import org.springframework.stereotype.Component;

@Component
public class SlotMapper {
    public static SlotResponseDto buildSlotResponseDto(TimeSlot s) {
        return SlotResponseDto.builder()
                .id(s.getId())
                .endTime(s.getEndTime())
                .startTime(s.getStartTime())
                .reserved(s.isBusy())
                .build();
    }

    public static TimeSlot buildTimeSlot(SlotRequestDto request, String idempotencyKey) {
        return TimeSlot.builder()
                .userId(request.getUserId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .busy(false)
                .idempotencyKey(idempotencyKey)
                .build();
    }
}
