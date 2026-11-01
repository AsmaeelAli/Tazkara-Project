package com.asmaeel.tazkara.event.model.entity;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "EVENTS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"owner_id", "title"})
        })
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id",nullable = false)
    private UUID ownerId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @NotBlank
    @Column(nullable = false)
    private String venue;

    @NotBlank
    @Column(nullable = false)
    private String city;

    private static final Duration MIN_LEAD_TIME = Duration.ofHours(24);

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;
    @NotNull
    @Column(name = "start_date_time",nullable = false)
    private Instant startDateTime;

    public EventEntity(){
    }

    public EventEntity(UUID ownerId, String title, String description, String venue, String city, Instant startDateTime) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.city = city;
        this.startDateTime = startDateTime;
        this.status = EventStatus.DRAFT;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
    }

    public String getCity() {
        return city;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void assertCanBePublished(Instant now) {
        if (status != EventStatus.DRAFT) {
            throw new BusinessRuleException("Only draft events can be published");
        }
        assertStartsAtLeast24HoursAhead(now);
    }

    public void publish() {
        this.status = EventStatus.PUBLISHED;
    }

    public void cancel() {
        if (status != EventStatus.PUBLISHED) {
            throw new BusinessRuleException("Only published events can be cancelled");
        }
        this.status = EventStatus.CANCELLED;
    }

    public void assertCanBeEdited() {
        if (status != EventStatus.DRAFT) {
            throw new BusinessRuleException("Only draft events can be edited");
        }
    }

    private void assertStartsAtLeast24HoursAhead(Instant now) {
        if (startDateTime.isBefore(now.plus(MIN_LEAD_TIME))) {
            throw new BusinessRuleException("Event must start at least 24 hours from now");
        }
    }
}
