package com.asmaeel.tazkara.event.usecase.ticket;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.TicketTypeRequestDto;
import com.asmaeel.tazkara.event.model.dto.TicketTypeResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import com.asmaeel.tazkara.event.usecase.event.CheckEventOwnerService;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import com.asmaeel.tazkara.shared.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateTicketTypeService {

    private final TicketTypeRepo ticketRepo;
    private final CheckEventOwnerService checker;

    public CreateTicketTypeService(CheckEventOwnerService checker , TicketTypeRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
        this.checker = checker;
    }

    @Transactional
    public TicketTypeResponseDto execute(UUID eventId, TicketTypeRequestDto request) {

        EventEntity event = checker.execute(eventId);

        if(event.getStatus() != EventStatus.DRAFT){
            throw new BusinessRuleException("Ticket types can only be added to events in DRAFT status");
        }

        if (!ticketRepo.hasAvailableTicketTypeSlots(eventId)) {
            throw new ConflictException("The event has reached the maximum limit of 5 ticket types");
        }

        if(ticketRepo.existsByEventIdAndName(eventId, request.name())){
            throw new ConflictException("Ticket type name already exists for this event");
        }

        TicketTypeEntity ticket = new TicketTypeEntity(
                request.name(),
                request.price(),
                request.quantity(),
                eventId
        );

        ticketRepo.save(ticket);

        return new TicketTypeResponseDto(
                ticket.getId(),
                ticket.getName(),
                ticket.getPrice(),
                ticket.getQuantity()
        );
    }
}