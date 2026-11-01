package com.asmaeel.tazkara.booking.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "booking_item")
public class BookingItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "booking_id" ,nullable = false)
    private UUID bookingId;

    @Column(name = "ticket_type_id", nullable = false)
    private UUID ticketTypeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public BookingItemEntity(){

    }

    public BookingItemEntity(UUID bookingId, UUID ticketTypeId, String name, int quantity, BigDecimal price, BigDecimal lineTotal) {
        this.bookingId = bookingId;
        this.ticketTypeId = ticketTypeId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.lineTotal = lineTotal;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getTicketTypeId() {
        return ticketTypeId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
