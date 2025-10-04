package com.doodle.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingRequestDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotEmpty
    private List<Long> participantIds;

    @NotEmpty
    private List<Long> slotIds;
}