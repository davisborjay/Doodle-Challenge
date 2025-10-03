package com.doodle.backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Table(name = "meeting")
@Data
@Builder
public class Meeting {

    @Id
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull
    @Column("start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column("end_time")
    private LocalDateTime endTime;
}