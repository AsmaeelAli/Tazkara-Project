package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.EventPublicationRequestDto;
import com.asmaeel.tazkara.booking.model.dto.EventTicketTypeRequestDto;
import com.asmaeel.tazkara.booking.model.dto.TicketDto;
import com.asmaeel.tazkara.booking.repository.TicketBatchRepo;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class CreateTicketsService {

    private final TicketBatchRepo ticketBatchRepo;
    private final TicketRepo ticketRepo;
    private final Integer batchSize;

    public CreateTicketsService(@Value("${booking.tickets.batch-size}")
                                Integer batchSize ,TicketRepo ticketRepo,
                                TicketBatchRepo ticketBatchRepo) {
        this.ticketRepo = ticketRepo;
        this.batchSize = batchSize;
        this.ticketBatchRepo = ticketBatchRepo;
    }

    @Transactional
    public void execute(EventPublicationRequestDto request) {

        UUID eventId = request.eventId();

        if(ticketRepo.existsByEventId(eventId)) {
            return;
        }

        List<TicketDto> tickets = convert(eventId,request.ticketsType());

        ticketBatchRepo.createTickets(batchSize, tickets);
    }

    private List<TicketDto> convert(UUID eventId, List<EventTicketTypeRequestDto> types) {
        return types.stream()
                .flatMap(t -> IntStream.range(0, t.quantity())
                        .mapToObj(i -> new TicketDto(UUID.randomUUID(), eventId, t.ticketTypeId())))
                .toList();
    }
}
