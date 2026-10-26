package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.model.dto.EventRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.usecase.event.CreateEvent;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final CreateEvent createEvent;

    public EventController(CreateEvent createEvent) {
        this.createEvent = createEvent;
    }

    @PostMapping
    public EventResponseDto newEvent(@RequestBody @Valid EventRequestDto request) {
        return createEvent.execute(request);
    }
}
