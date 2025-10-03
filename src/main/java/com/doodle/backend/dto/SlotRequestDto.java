package com.doodle.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SlotRequestDto {
    @NotNull
    private Long userId;

    @NotNull
    @Future(message = "startTime must be in the future")
    private LocalDateTime startTime;

    @NotNull
    @Future(message = "endTime must be in the future")
    private LocalDateTime endTime;
}
