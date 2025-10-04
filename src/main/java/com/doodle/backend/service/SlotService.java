package com.doodle.backend.service;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SlotService {
    Mono<SlotResponseDto> createSlot(SlotRequestDto request, String idempotencyKey);
    Flux<SlotResponseDto> getSlotsByUser(Long userId);
    Mono<Void> deleteSlotById(Long slotId);
    Mono<SlotResponseDto> updateSlot (Long slotId, SlotRequestDto request);
}
