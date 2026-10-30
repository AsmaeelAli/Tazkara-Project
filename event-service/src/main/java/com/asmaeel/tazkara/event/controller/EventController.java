package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.EventRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.usecase.event.CreateEvent;
import com.asmaeel.tazkara.event.usecase.event.GetMyEvents;
import com.asmaeel.tazkara.event.usecase.event.PublishEventService;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final GetMyEvents getMyEvents;
    private final CreateEvent createEvent;
    private final PublishEventService publishEventService;

    public EventController(CreateEvent createEvent, GetMyEvents getMyEvents, PublishEventService publishEventService) {
        this.createEvent = createEvent;
        this.getMyEvents = getMyEvents;
        this.publishEventService = publishEventService;
    }

    @PostMapping
    public EventResponseDto newEvent(@RequestBody @Valid EventRequestDto request) {
        return createEvent.execute(request);
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
        return getMyEvents.execute(status, page, size);
    }
}
