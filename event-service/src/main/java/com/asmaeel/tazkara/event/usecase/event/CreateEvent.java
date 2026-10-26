package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.model.dto.EventRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.shared.exception.ConflictException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateEvent {
    private final EventRepo eventRepo;

    public CreateEvent(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    public EventResponseDto execute(EventRequestDto request) {

        UUID organizerId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!eventRepo.hasAvailableEventSlots(organizerId)) {
            throw new ConflictException("You have reached the maximum limit of 10 events");
        }

        if(eventRepo.existsByOwnerIdAndTitle(organizerId , request.title())) {
            throw new ConflictException("You already have an event with this title");
        }

        EventEntity event = new EventEntity(
                organizerId,
                request.title(),
                request.description(),
                request.venue(),
                request.city(),
                request.startDateTime()
        );

        eventRepo.save(event);

        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getCity(),
                event.getStartDateTime(),
                event.getStatus()
        );
    }
}
