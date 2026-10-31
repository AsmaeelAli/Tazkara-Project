package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.EventPublicationRequestDto;
import com.asmaeel.tazkara.booking.model.dto.EventTicketTypeRequestDto;
import com.asmaeel.tazkara.booking.model.dto.TicketDto;
import com.asmaeel.tazkara.booking.model.entity.PublishedEventEntity;
import com.asmaeel.tazkara.booking.model.entity.PublishedTicketTypeEntity;
import com.asmaeel.tazkara.booking.repository.PublishedEventRepo;
import com.asmaeel.tazkara.booking.repository.PublishedTicketTypeRepo;
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

    private final Integer batchSize;
    private final TicketRepo ticketRepo;
    private final TicketBatchRepo ticketBatchRepo;
    private final PublishedEventRepo publishedEventRepo;
    private final PublishedTicketTypeRepo publishedTicketTypeRepo;

    public CreateTicketsService(@Value("${booking.tickets.batch-size}")
                                Integer batchSize ,TicketRepo ticketRepo,
                                TicketBatchRepo ticketBatchRepo ,
                                PublishedTicketTypeRepo publishedTicketTypeRepo,
                                PublishedEventRepo publishedEventRepo) {

        this.batchSize = batchSize;
        this.ticketRepo = ticketRepo;
        this.ticketBatchRepo = ticketBatchRepo;
        this.publishedEventRepo = publishedEventRepo;
        this.publishedTicketTypeRepo = publishedTicketTypeRepo;
    }

    @Transactional
    public void execute(EventPublicationRequestDto request) {

        UUID eventId = request.eventId();

        if(ticketRepo.existsByEventId(eventId)) {
            return;
        }

        PublishedEventEntity eventData = new PublishedEventEntity(
                eventId,
                request.eventTitle(),
                request.eventStartTime()
        );

        List<PublishedTicketTypeEntity> ticketTypes = request.ticketsType().stream()
                .map(t -> new PublishedTicketTypeEntity(
                        t.ticketTypeId(),
                        eventId,
                        t.name(),
                        t.price(),
                        t.quantity()
                ))
                .toList();

        publishedEventRepo.save(eventData);
        publishedTicketTypeRepo.saveAll(ticketTypes);

        List<TicketDto> tickets = convert(eventId, request.ticketsType());

        ticketBatchRepo.createTickets(batchSize, tickets);
    }

    private List<TicketDto> convert(UUID eventId, List<EventTicketTypeRequestDto> types) {
        return types.stream()
                .flatMap(t -> IntStream.range(0, t.quantity())
                        .mapToObj(i -> new TicketDto(UUID.randomUUID(), eventId, t.ticketTypeId())))
                .toList();
    }
}
