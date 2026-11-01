package com.asmaeel.tazkara.event.model.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "updated_event_data")
public class EventBookingSummaryEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "ticket_type_id", nullable = false , unique = true)
    private UUID ticketTypeId;

    @Column(name = "available")
    private Integer available;

    @Column(name = "held")
    private Integer held;

    @Column(name = "sold")
    private Integer sold;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    public EventBookingSummaryEntity() {
    }

    public EventBookingSummaryEntity(UUID eventId, UUID ticketTypeId,
                                     Integer available, Integer held, Integer sold,
                                     Instant sentAt) {
        this.eventId = eventId;
        this.ticketTypeId = ticketTypeId;
        this.available = available;
        this.held = held;
        this.sold = sold;
        this.lastUpdated = sentAt;
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

    public Integer getAvailable() {
        return available;
    }

    public Integer getHeld() {
        return held;
    }

    public Integer getSold() {
        return sold;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void updateCounts(Integer available, Integer held, Integer sold, Instant sentAt) {
        if (sentAt.isBefore(this.lastUpdated)) {
            return;
        }
        this.available = available;
        this.held = held;
        this.sold = sold;
        this.lastUpdated = sentAt;
    }



}
