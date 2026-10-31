package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.common.PublishedEventStatus;
import com.asmaeel.tazkara.booking.model.dto.BookingRequestDto;
import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.dto.ItemRequestDto;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.PublishedEventEntity;
import com.asmaeel.tazkara.booking.model.entity.PublishedTicketTypeEntity;
import com.asmaeel.tazkara.booking.repository.PublishedTicketTypeRepo;
import com.asmaeel.tazkara.booking.repository.PublishedEventRepo;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PlaceBookingService {
    private final Duration holdDuration;
    private final BookingRepo bookingRepo;
    private final ReferenceGenerator generator;
    private final PublishedEventRepo publishedEventRepo;
    private final HoldTicketsService holdTicketsService;
    private final BookingResponseMapper bookingResponseMapper;
    private final ConfirmTicketsService confirmTicketsService;
    private final PublishedTicketTypeRepo publishedTicketTypeRepo;


    public PlaceBookingService(PublishedEventRepo publishedEventRepo,
                               ConfirmTicketsService confirmTicketsService,
                               PublishedTicketTypeRepo publishedTicketTypeRepo,
                               BookingRepo bookingRepo, TicketRepo ticketRepo ,
                               BookingResponseMapper bookingResponseMapper,
                               HoldTicketsService holdTicketsService, ReferenceGenerator generator ,
                               @Value("${booking.hold-duration}") Duration holdDuration) {

        this.generator = generator;
        this.bookingRepo = bookingRepo;
        this.holdDuration = holdDuration;
        this.publishedEventRepo = publishedEventRepo;
        this.holdTicketsService = holdTicketsService;
        this.confirmTicketsService = confirmTicketsService;
        this.bookingResponseMapper = bookingResponseMapper;
        this.publishedTicketTypeRepo = publishedTicketTypeRepo;
    }

    @Transactional
    public BookingResponseDto execute(BookingRequestDto request) {

        Instant now = Instant.now();
        Instant holdExpiresAt = now.plus(holdDuration);


        UUID attendeeId = UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());

        // 1. Validate published event
        PublishedEventEntity event = publishedEventRepo.findById(request.eventId())
                .orElseThrow(() -> new BusinessRuleException("Event is not published"));

        if(event.getStatus() == PublishedEventStatus.CANCELLED){
            throw new BusinessRuleException("Event has been Cancelled");
        }

        if(event.getEventStartTime().isBefore(Instant.now())){
            throw new BusinessRuleException("Sorry, this event has already started. Better luck next time!");
        }


        List<UUID> ids = request.items().stream()
                .map(ItemRequestDto::ticketTypeId)
                .toList();

        // 2. Validate requested ticket types and quantity
        if (publishedTicketTypeRepo.countByEventIdAndIdIn(event.getEventId(), ids) != ids.size()) {
            throw new BusinessRuleException("One or more ticket types do not belong to this event");
        }

        int requestedQuantity = request.items().stream()
                .mapToInt(ItemRequestDto::quantity)
                .sum();

        bookingRepo.lockAttendeeEvent(attendeeId,event.getEventId());

        if (!bookingRepo.canBook(attendeeId, request.eventId(), requestedQuantity)) {
            throw new BusinessRuleException("A maximum of 6 tickets is allowed per event per user");
        }



        BigDecimal total = BigDecimal.ZERO;

        for(ItemRequestDto item : request.items()){
           total = total.add(publishedTicketTypeRepo.calculateTicket(event.getEventId(),item.ticketTypeId(),item.quantity()));
        }


        // 3. Check ticket availability
        UUID bookingId = UUID.randomUUID();
        holdTicketsService.execute(bookingId, request.eventId(), request.items());

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            confirmTicketsService.execute(bookingId);
        }


        // 4. Create booking
        String reference = generator.execute();
        BookingEntity booking = new BookingEntity(
                bookingId,
                event.getEventId(),
                attendeeId,
                event.getEventTitle(),
                reference,
                event.getEventStartTime(),
                total,
                holdExpiresAt,
                now,
                requestedQuantity
        );
        bookingRepo.save(booking);

        List<PublishedTicketTypeEntity> types =
                publishedTicketTypeRepo.findAllById(ids);

        return bookingResponseMapper.toResponse(booking, types, request.items());
    }
}


