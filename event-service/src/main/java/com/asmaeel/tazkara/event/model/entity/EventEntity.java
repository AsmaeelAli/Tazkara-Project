package com.asmaeel.tazkara.event.model.entity;

import com.asmaeel.tazkara.event.common.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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

    @NotNull
    @Column(name = "start_date_time",nullable = false)
    private LocalDateTime startDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    public EventEntity(){
    }

    public EventEntity(UUID ownerId, String title, String description, String venue, String city, LocalDateTime startDateTime) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.city = city;
        this.startDateTime = startDateTime;
        this.status = EventStatus.DRAFT;
    }
}
