package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.CreateEventRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.model.dto.UpdateEventRequestDto;
import com.asmaeel.tazkara.event.usecase.event.CreateEventService;
import com.asmaeel.tazkara.event.usecase.event.ListMyEventsService;
import com.asmaeel.tazkara.event.usecase.event.PublishEventService;
import com.asmaeel.tazkara.event.usecase.event.UpdateDraftEventService;
import com.asmaeel.tazkara.shared.dto.MessageResponse;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final CreateEventService createEventService;
    private final UpdateDraftEventService updateDraftEventService;
    private final PublishEventService publishEventService;
    private final ListMyEventsService listMyEventsService;

    public EventController(CreateEventService createEventService,
                           UpdateDraftEventService updateDraftEventService,
                           PublishEventService publishEventService,
                           ListMyEventsService listMyEventsService) {

        this.createEventService = createEventService;
        this.updateDraftEventService = updateDraftEventService;
        this.publishEventService = publishEventService;
        this.listMyEventsService = listMyEventsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto newEvent(@RequestBody @Valid CreateEventRequestDto request) {
        return createEventService.execute(request);
    }

    @PutMapping("/{eventId}")
    public EventResponseDto updateEvent(@PathVariable UUID eventId,
                                        @RequestBody @Valid UpdateEventRequestDto request) {
        return updateDraftEventService.execute(eventId, request);
    }

    @PostMapping("/{eventId}/publish")
    public MessageResponse publishEvent(@PathVariable UUID eventId) {
        return new MessageResponse(publishEventService.execute(eventId));
    }

    @PostMapping("/{eventId}/cancel")
    public MessageResponse cancelEvent(@PathVariable UUID eventId) {
        return null;
    }

    @GetMapping
    public PageResponse<EventResponseDto> myEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return listMyEventsService.execute(status, Math.max(page, 0), Math.max(1, Math.min(size, 50)));
    }
}