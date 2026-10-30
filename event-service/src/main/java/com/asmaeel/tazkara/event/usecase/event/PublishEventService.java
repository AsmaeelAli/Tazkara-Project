package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.bookingclient.BookingClient;
import com.asmaeel.tazkara.event.model.dto.EventPublicationRequestDto;
import com.asmaeel.tazkara.event.model.dto.EventTicketTypeRequestDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PublishEventService {

    private final EventRepo eventRepo;
    private final TicketTypeRepo ticketRepo;
    private final CheckEventOwnerService checker;
    private final BookingClient bookingClient;

    public PublishEventService(EventRepo eventRepo, TicketTypeRepo ticketRepo, CheckEventOwnerService checker, BookingClient bookingClient) {
        this.eventRepo = eventRepo;
        this.ticketRepo = ticketRepo;
        this.checker = checker;
        this.bookingClient = bookingClient;
    }


    public String execute(UUID eventId) {

        EventEntity event = checker.execute(eventId);

        event.assertCanBePublished(Instant.now());

        List<TicketTypeEntity> ticketTypeEntityList =
                ticketRepo.findAllByEventId(event.getId());

        List<EventTicketTypeRequestDto> ticketsDto =
                ticketTypeEntityList
                        .stream()
                        .map( t -> new EventTicketTypeRequestDto(
                                t.getId(),
                                t.getName(),
                                t.getPrice(),
                                t.getQuantity()
                        )).toList();

        EventPublicationRequestDto pubReqDto = new EventPublicationRequestDto(
                event.getId(),
                ticketsDto
        );

        bookingClient.send(pubReqDto);


        event.publish();
        eventRepo.save(event);
        return "Event published successfully";

    }
}
