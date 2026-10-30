package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.*;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UpdateDraftEventService {

    private final EventRepo eventRepo;
    private final TicketTypeRepo ticketTypeRepo;
    private final CheckEventOwnerService checkEventOwnerService;

    public UpdateDraftEventService(
            EventRepo eventRepo,
            TicketTypeRepo ticketTypeRepo,
            CheckEventOwnerService checkEventOwnerService
    ) {
        this.eventRepo = eventRepo;
        this.ticketTypeRepo = ticketTypeRepo;
        this.checkEventOwnerService = checkEventOwnerService;
    }

    @Transactional
    public EventResponseDto execute(UUID eventId, UpdateEventRequestDto request) {

        EventEntity event = checkEventOwnerService.execute(eventId);

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessRuleException("You can update a draft Event only");
        }

        if (request.startDateTime().isBefore(Instant.now().plus(Duration.ofHours(72)))) {
            throw new BusinessRuleException("Event must start at least 3 days in the future");
        }

        UUID currentUserId = UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName());

        if (eventRepo.existsByTitleAndOwnerIdAndIdNot(request.title(), currentUserId , eventId)) {
            throw new BusinessRuleException("Title already exists in another Event");
        }

        List<UUID> ticketIds = request.tickets()
                .stream()
                .map(UpdateTicketTypeRequestDto::id)
                .toList();

        if (new HashSet<>(ticketIds).size() != ticketIds.size()) {
            throw new BusinessRuleException("Duplicate ticket IDs");
        }

        List<TicketTypeEntity> existingTickets =
                ticketTypeRepo.findAllByEventIdAndIdIn(eventId, ticketIds);

        if (existingTickets.size() != ticketIds.size()) {
            throw new BusinessRuleException("One or more ticket types do not exist in this Event");
        }

        Set<String> names = new HashSet<>();

        boolean hasDuplicates = request.tickets().stream()
                .map(UpdateTicketTypeRequestDto::name)
                .anyMatch(name -> !names.add(name));

        if (hasDuplicates) {
            throw new BusinessRuleException("Duplicate TicketType names");
        }


        event.setTitle(request.title());
        event.setDescription(request.description().trim());
        event.setVenue(request.venue().trim());
        event.setCity(request.city().trim());
        event.setStartDateTime(request.startDateTime());

        // Update Ticket Types
        Map<UUID, TicketTypeEntity> ticketMap = existingTickets
                .stream()
                .collect(Collectors.toMap(
                        TicketTypeEntity::getId,
                        Function.identity()
                ));

        for (UpdateTicketTypeRequestDto ticket : request.tickets()) {

            TicketTypeEntity entity = ticketMap.get(ticket.id());

            entity.setName(ticket.name().trim());
            entity.setPrice(ticket.price());
            entity.setQuantity(ticket.quantity());
        }

        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getCity(),
                event.getStartDateTime(),
                event.getStatus(),
                existingTickets.stream()
                        .map(ticket -> new TicketTypeResponseDto(
                                ticket.getId(),
                                ticket.getName(),
                                ticket.getPrice(),
                                ticket.getQuantity()
                        ))
                        .toList()
        );
    }
}
