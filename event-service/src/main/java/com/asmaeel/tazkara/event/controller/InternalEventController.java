package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.usecase.event.GetEventOwnerService;
import com.asmaeel.tazkara.shared.dto.OwnerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/events")
public class InternalEventController {

    private final GetEventOwnerService getEventOwnerService;

    public InternalEventController(GetEventOwnerService getEventOwnerService) {
        this.getEventOwnerService = getEventOwnerService;
    }

    @GetMapping("/{eventId}/owner")
    public OwnerResponse owner(@PathVariable UUID eventId) {
        return getEventOwnerService.execute(eventId);
    }
}
