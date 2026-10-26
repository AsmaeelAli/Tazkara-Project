package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.shared.exception.ForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class EventOwnershipChecker {

    private final EventRepo eventRepo;

    public EventOwnershipChecker(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    public EventEntity execute(UUID eventId) {

        EventEntity event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));

        UUID currentUserId = UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());

        if (!currentUserId.equals(event.getOwnerId())) {
            throw new ForbiddenException("You are not authorized to manage this event");
        }

        return event;
    }
}
