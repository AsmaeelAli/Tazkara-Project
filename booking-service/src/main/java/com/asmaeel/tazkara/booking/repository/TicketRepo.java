package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.common.TicketStatus;
import com.asmaeel.tazkara.booking.model.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepo extends JpaRepository<TicketEntity, UUID>{

    boolean existsByEventId(UUID eventId);

    List<TicketEntity> findAllByBookingIdAndStatus(UUID bookingId, TicketStatus status);

    @Query(value = """
    SELECT * FROM tickets t
    WHERE t.event_id = :eventId
      AND t.ticket_type_id = :ticketTypeId
      AND t.status = 'FREE'
    LIMIT :quantity
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<TicketEntity> lockFreeTickets(UUID eventId, UUID ticketTypeId, int quantity);

    List<TicketEntity> findAllByBookingId(UUID bookingId);
}
