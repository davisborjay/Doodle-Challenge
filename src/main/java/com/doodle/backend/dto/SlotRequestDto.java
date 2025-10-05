package com.doodle.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SlotRequestDto {
    @NotNull
    @Schema(example = "1")
    private Long userId;

    @NotNull
    @Future(message = "startTime must be in the future")
    @Schema(type = "string", example = "2025-12-05T10:00:00")
    private LocalDateTime startTime;

    @NotNull
    @Future(message = "endTime must be in the future")
    @Schema(type = "string", example = "2025-12-05T10:30:00")
    private LocalDateTime endTime;

    @AssertTrue(message = "End time must be after start time")
    @JsonIgnore
    public boolean isEndAfterStart() {
        return endTime != null && startTime != null && endTime.isAfter(startTime);
    }
}