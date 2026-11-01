package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.common.PublishedEventStatus;
import com.asmaeel.tazkara.booking.model.dto.BookingChangedEventDto;
import com.asmaeel.tazkara.booking.model.dto.BookingSenderDto;
import com.asmaeel.tazkara.booking.model.dto.PaymentRequestDto;
import com.asmaeel.tazkara.booking.model.dto.PaymentResponseDto;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.PublishedEventEntity;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import com.asmaeel.tazkara.booking.repository.PublishedEventRepo;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PayBookingService {

    private final BookingRepo bookingRepo;
    private final PublishedEventRepo publishedEventRepo;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConfirmTicketsService confirmTicketsService;

    public PayBookingService(BookingRepo bookingRepo,
                             PublishedEventRepo publishedEventRepo,
                             ConfirmTicketsService confirmTicketsService,
                             ApplicationEventPublisher applicationEventPublisher) {


        this.bookingRepo = bookingRepo;
        this.publishedEventRepo = publishedEventRepo;
        this.confirmTicketsService = confirmTicketsService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public PaymentResponseDto execute(String reference, PaymentRequestDto request) {

        Instant now = Instant.now();

        UUID attendeeId = UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());

        BookingEntity booking = bookingRepo.findByReferenceAndAttendeeId(reference, attendeeId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        PublishedEventEntity event = publishedEventRepo.findById(booking.getEventId())
                .orElseThrow(() -> new NoSuchElementException("Event is not available"));

        if (event.getStatus() == PublishedEventStatus.CANCELLED) {
            throw new BusinessRuleException("Event has been cancelled");
        }


        // انت حجزتهم وفرضاااا الافنت بلشش وبدك تدفع بزبطش راحت عليك
        if (event.getEventStartTime().isBefore(now)) {
            throw new BusinessRuleException("Event has already started");
        }

        booking.confirm(now);

        confirmTicketsService.execute(booking.getId());

        applicationEventPublisher.publishEvent(new BookingSenderDto(
                booking.getEmail(),
                booking.getReference(),
                booking.getStatus(),
                booking.getEventTitle()));


        applicationEventPublisher.publishEvent(new BookingChangedEventDto(booking.getEventId()));
        return new PaymentResponseDto(
                booking.getReference(),
                booking.getStatus(),
                booking.getTotalAmount(),
                booking.getConfirmedAt()
        );
    }
}
