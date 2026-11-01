package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.model.dto.CreateEventRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.model.dto.TicketTypeRequestDto;
import com.asmaeel.tazkara.event.model.dto.TicketTypeResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import com.asmaeel.tazkara.shared.exception.ConflictException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CreateEventService {
    private final EventRepo eventRepo;
    private final TicketTypeRepo ticketTypeRepo;


    public CreateEventService(TicketTypeRepo ticketTypeRepo, EventRepo eventRepo) {
        this.eventRepo = eventRepo;
        this.ticketTypeRepo = ticketTypeRepo;
    }

    @Transactional
    public EventResponseDto execute(CreateEventRequestDto request) {

        if (request.startDateTime().isBefore(Instant.now().plus(Duration.ofHours(72)))) {
            throw new BusinessRuleException("Event must start at least 3 days in the future");
        }

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

        List<TicketTypeResponseDto> list = listTickets(event.getId(), request.tickets());


        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getCity(),
                event.getStartDateTime(),
                event.getStatus(),
                list
        );
    }

    private List<TicketTypeResponseDto> listTickets(UUID id, List<TicketTypeRequestDto> list) {

        Set<String> names = new HashSet<>();

        boolean hasDuplicates = list.stream()
                .map(TicketTypeRequestDto::name)
                .anyMatch(name -> !names.add(name));

        if (hasDuplicates) {
            throw new BusinessRuleException("Duplicate TicketType names");
        }

        List<TicketTypeEntity> ticketTypeList = list.stream()
                .map(req -> new TicketTypeEntity(
                        req.name(),
                        req.price(),
                        req.quantity(),
                        id
                ))
                .toList();

        ticketTypeRepo.saveAll(ticketTypeList);

        return ticketTypeList.stream()
                .map(ticket -> new TicketTypeResponseDto(
                        ticket.getId(),
                        ticket.getName(),
                        ticket.getPrice(),
                        ticket.getQuantity()
                ))
                .toList();
    }
}
