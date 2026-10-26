package com.asmaeel.tazkara.event.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TICKET_TYPE",uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"event_id","name"}
        )})
public class TicketTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false , precision = 10 , scale = 2)
    @DecimalMin("0.00")
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    @Max(10000)
    private Integer quantity;

    @NotNull
    @Column(name = "event_id" , nullable = false)
    private UUID eventId;

    public TicketTypeEntity() {
    }

    public TicketTypeEntity(String name, BigDecimal price, Integer quantity, UUID eventId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.eventId = eventId;
    }
}
