package com.doodle.backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Table("time_slot")
public class TimeSlot {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @NotNull
    @Column("start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column("end_time")
    private LocalDateTime endTime;

    @Column("is_busy")
    private boolean busy;

    @Column("meeting_id")
    private Long meetingId;
}