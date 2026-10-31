package com.asmaeel.tazkara.booking.model.entity;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.common.CancellationReason;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "BOOKINGS",
        indexes = {
                @Index(name = "idx_booking_attendee_event", columnList = "attendee_id,event_id"),
                @Index(name = "idx_booking_status_hold", columnList = "status,hold_expires_at")
        })
public class BookingEntity {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "attendee_id", nullable = false)
    private UUID attendeeId;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(nullable = false, unique = true, length = 16)
    private String reference;

    @Column(name = "event_start_time", nullable = false)
    private Instant eventStartTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_reason")
    private CancellationReason cancellationReason;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "hold_expires_at", nullable = false)
    private Instant holdExpiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    protected BookingEntity() {
    }

    public BookingEntity(UUID id, UUID eventId, UUID attendeeId,
                         String eventTitle, String reference, Instant eventStartTime,
                         BigDecimal totalAmount, Instant holdExpiresAt, Instant createdAt ,Integer quantity) {
        this.id = id;
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.eventTitle = eventTitle;
        this.reference = reference;
        this.eventStartTime = eventStartTime;
        this.totalAmount = totalAmount;
        this.holdExpiresAt = holdExpiresAt;
        this.createdAt = createdAt;
        this.quantity = quantity;

        if (totalAmount.compareTo(BigDecimal.ZERO) == 0.00) {
            this.status = BookingStatus.CONFIRMED;
            this.confirmedAt = createdAt;
        } else {
            this.status = BookingStatus.PENDING_PAYMENT;
        }
    }

    public void confirm(Instant now) {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new BusinessRuleException("Only a booking waiting for payment can be paid");
        }

        if (!now.isBefore(holdExpiresAt)) {
            throw new BusinessRuleException("The payment time for this booking has passed");
        }
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = now;
    }

    public void expire(Instant now) {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new BusinessRuleException("Only a booking waiting for payment can expire");
        }

        this.status = BookingStatus.EXPIRED;
    }

    public void cancel(CancellationReason reason, BigDecimal refund) {
        if (status != BookingStatus.PENDING_PAYMENT && status != BookingStatus.CONFIRMED) {
            throw new BusinessRuleException("This booking can no longer be cancelled");
        }
        // Refund exists only if something was paid
        if (status == BookingStatus.CONFIRMED) {
            this.refundAmount = refund;
        }
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getAttendeeId() {
        return attendeeId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getReference() {
        return reference;
    }

    public Instant getEventStartTime() {
        return eventStartTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public CancellationReason getCancellationReason() {
        return cancellationReason;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public Instant getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public Integer getQuantity() {
        return quantity;
    }
}