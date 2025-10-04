package com.doodle.backend.service.impl;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.entity.TimeSlot;
import com.doodle.backend.repository.SlotRepository;
import com.doodle.backend.service.SlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SlotServiceImpl implements SlotService {
    @Autowired
    private SlotRepository slotRepository;

    @Override
    public Mono<SlotResponseDto> createSlot(SlotRequestDto request, String idempotencyKey) {
        log.info("calling slotRepository.findOverlappingSlot for userId={}", request.getUserId());

        return slotRepository.findByIdempotencyKey(idempotencyKey)
                .map(SlotServiceImpl::buildSlotResponseDto)
                .switchIfEmpty(
                        slotRepository.findOverlappingSlot(request.getStartTime(), request.getEndTime())
                                .flatMap(existing ->
                                        Mono.<SlotResponseDto>error(new IllegalArgumentException("Overlapping slot exists"))
                                )
                                .switchIfEmpty(Mono.defer(() ->
                                        slotRepository.save(buildTimeSlot(request, idempotencyKey))
                                                .map(SlotServiceImpl::buildSlotResponseDto)
                                ))
                );
    }

    private static TimeSlot buildTimeSlot(SlotRequestDto request, String idempotencyKey) {
        return TimeSlot.builder()
                .userId(request.getUserId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .busy(false)
                .idempotencyKey(idempotencyKey)
                .build();
    }

    @Override
    public Flux<SlotResponseDto> getSlotsByUser(Long userId) {
        log.info("calling slotRepository.findByUserId for userId={}", userId);
        return slotRepository.findByUserId(userId)
                .map(SlotServiceImpl::buildSlotResponseDto);
    }

    @Override
    public Mono<Void> deleteSlotById(Long slotId) {
        return slotRepository.deleteById(slotId);
    }

    @Override
    public Mono<SlotResponseDto> updateSlot(Long slotId, SlotRequestDto request) {
        return slotRepository.findById(slotId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Slot not found")))
                .flatMap(existing -> {
                    existing.setStartTime(request.getStartTime());
                    existing.setEndTime(request.getEndTime());
                    existing.setUserId(request.getUserId());
                    return slotRepository.save(existing);
                })
                .map(SlotServiceImpl::buildSlotResponseDto);
    }

    private static SlotResponseDto buildSlotResponseDto(TimeSlot s) {
        return SlotResponseDto.builder()
                .id(s.getId())
                .endTime(s.getEndTime())
                .startTime(s.getStartTime())
                .reserved(s.isBusy())
                .build();
    }
}