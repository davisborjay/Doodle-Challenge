package com.doodle.backend.service.impl;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.entity.TimeSlot;
import com.doodle.backend.repository.SlotRepository;
import com.doodle.backend.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SlotServiceImpl implements SlotService {
    @Autowired
    private SlotRepository slotRepository;

    @Override
    public Mono<SlotResponseDto> createSlot(SlotRequestDto request) {
        return slotRepository.findOverlappingSlot(request.getStartTime(), request.getEndTime())
                .flatMap(existing ->
                        Mono.<SlotResponseDto>error(new IllegalArgumentException("Overlapping slot exists"))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    TimeSlot slot = TimeSlot.builder()
                            .userId(request.getUserId())
                            .startTime(request.getStartTime())
                            .endTime(request.getEndTime())
                            .busy(false)
                            .build();
                    return slotRepository.save(slot)
                            .map(s -> SlotResponseDto.builder()
                                    .id(s.getId())
                                    .endTime(s.getEndTime())
                                    .startTime(s.getStartTime())
                                    .reserved(s.isBusy())
                                    .build());
                }));
    }
}