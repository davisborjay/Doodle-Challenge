package com.doodle.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SlotResponseDto {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean reserved;
}
