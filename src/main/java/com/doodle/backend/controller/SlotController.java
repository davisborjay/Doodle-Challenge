package com.doodle.backend.controller;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.service.SlotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/slots")
@Slf4j
public class SlotController {

    @Autowired
    private SlotService slotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SlotResponseDto> createSlot(
            @Valid @RequestBody SlotRequestDto request,
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {
        log.info("POST /api/v1/slots with Idempotency-Key={} | Request={}", idempotencyKey, request);
        return slotService.createSlot(request, idempotencyKey);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<SlotResponseDto> getSlotsByUser(@PathVariable("userId") Long userId) {
        log.info("GET /api/v1/slots/{}", userId);
        return slotService.getSlotsByUser(userId);
    }

    @DeleteMapping("/{slotId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSlotsById(@PathVariable("slotId") Long slotId) {
        log.info("DELETE /api/v1/slots/{}", slotId);
        return slotService.deleteSlotById(slotId);
    }

    @PutMapping("/{slotId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SlotResponseDto> updateSlot(
            @PathVariable Long slotId,
            @RequestBody @Valid SlotRequestDto request) {
        log.info("PUT /api/v1/slots/{} with data: {}", slotId, request);
        return slotService.updateSlot(slotId, request);
    }
}