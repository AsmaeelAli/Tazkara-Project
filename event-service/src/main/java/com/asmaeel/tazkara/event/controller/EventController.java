package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.CreateEventRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.usecase.event.CreateEventService;
import com.asmaeel.tazkara.event.usecase.event.ListMyEventsService;
import com.asmaeel.tazkara.event.usecase.event.PublishEventService;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final ListMyEventsService listMyEventsService;
    private final CreateEventService createEventService;
    private final PublishEventService publishEventService;

    public EventController(CreateEventService createEventService, ListMyEventsService listMyEventsService, PublishEventService publishEventService) {
        this.createEventService = createEventService;
        this.listMyEventsService = listMyEventsService;
        this.publishEventService = publishEventService;
    }

    @PostMapping
    public EventResponseDto newEvent(@RequestBody @Valid CreateEventRequestDto request) {
        return createEventService.execute(request);
    }

    @PostMapping("/{eventId}/publish")
    public ResponseEntity<String> publishEvent(@PathVariable UUID eventId) {
        String message = publishEventService.execute(eventId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<String> cancelEvent(@PathVariable UUID eventId) {
        return null;
    }

    @GetMapping()
    public PageResponse<EventResponseDto> myEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return listMyEventsService.execute(status, page, size);
    }
}
