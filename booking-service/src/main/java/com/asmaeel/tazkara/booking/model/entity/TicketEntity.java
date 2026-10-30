package com.asmaeel.tazkara.booking.model.entity;

import com.asmaeel.tazkara.booking.common.TicketStatus;
import com.asmaeel.tazkara.shared.exception.InternalSystemException;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tickets", indexes = @Index(columnList = "event_id, ticket_type_id, status"))
public class TicketEntity {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "ticket_type_id", nullable = false)
    private UUID ticketTypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(name = "booking_id")
    private UUID bookingId;

    protected TicketEntity() {

    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getTicketTypeId() {
        return ticketTypeId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public void hold(UUID bookingId) {
        changeStatus(TicketStatus.HELD);
        this.bookingId = bookingId;
    }

    public void sell() {
        changeStatus(TicketStatus.SOLD);
    }

    public void release() {
        changeStatus(TicketStatus.FREE);
        this.bookingId = null;
    }

    private void changeStatus(TicketStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InternalSystemException("Ticket cannot move from " + status + " to " + target);
        }
        this.status = target;
    }
}

