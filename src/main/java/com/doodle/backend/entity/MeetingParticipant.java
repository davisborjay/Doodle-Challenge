package com.doodle.backend.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("meeting_participant")
public class MeetingParticipant {
    @Column("meeting_id")
    private Long meetingId;

    @Column("user_id")
    private Long userId;
}