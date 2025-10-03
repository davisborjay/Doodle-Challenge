package com.doodle.backend.service;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import reactor.core.publisher.Mono;

public interface SlotService {
    Mono<SlotResponseDto> createSlot(SlotRequestDto request);
}
