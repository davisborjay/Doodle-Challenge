package com.doodle.backend.controller;

import com.doodle.backend.dto.SlotRequestDto;
import com.doodle.backend.dto.SlotResponseDto;
import com.doodle.backend.service.SlotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/slots")
@Slf4j
public class SlotController {

    @Autowired
    private SlotService slotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SlotResponseDto> createSlot(@Valid @RequestBody SlotRequestDto request) {
        log.info("POST: /api/v1/slots with data: {}", request);
        return slotService.createSlot(request);
    }
}
