package com.doodle.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BookingRequestDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Future(message = "startTime must be in the future")
    @Schema(type = "string", example = "2025-12-05T10:00:00")
    private LocalDateTime startTime;

    @NotNull
    @Future(message = "endTime must be in the future")
    @Schema(type = "string", example = "2025-12-05T10:30:00")
    private LocalDateTime endTime;

    @NotEmpty
    private List<Long> participantIds;

    @NotEmpty
    private List<Long> slotIds;

    @AssertTrue(message = "End time must be after start time")
    @JsonIgnore
    public boolean isEndAfterStart() {
        return endTime != null && startTime != null && endTime.isAfter(startTime);
    }
}