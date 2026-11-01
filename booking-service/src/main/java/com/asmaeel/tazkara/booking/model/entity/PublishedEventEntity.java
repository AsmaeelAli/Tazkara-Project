package com.asmaeel.tazkara.booking.model.entity;

import com.asmaeel.tazkara.booking.common.PublishedEventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "published_events")
public class PublishedEventEntity {

    @Id
    @Column(name = "event_id" , nullable = false)
    private UUID eventId;

    @NotBlank
    @Column(name = "event_title",nullable = false)
    private String eventTitle;

    @Column(name = "event_start_time", nullable = false)
    private Instant eventStartTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishedEventStatus status;

    public PublishedEventEntity() {
    }

    public PublishedEventEntity(UUID eventId, String eventTitle, Instant eventStartTime) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventStartTime = eventStartTime;
        this.status = PublishedEventStatus.PUBLISHED;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public Instant getEventStartTime() {
        return eventStartTime;
    }

    public PublishedEventStatus getStatus() {
        return status;
    }
}
