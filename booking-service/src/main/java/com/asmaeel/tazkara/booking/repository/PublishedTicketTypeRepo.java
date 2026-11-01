package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.model.entity.PublishedTicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface PublishedTicketTypeRepo extends JpaRepository<PublishedTicketTypeEntity, UUID> {
    Integer countByEventIdAndIdIn(UUID eventId, Collection<UUID> ids);

    @Query("""
    SELECT pt.price * :quantity FROM PublishedTicketTypeEntity pt WHERE pt.eventId = :eventId AND pt.id = :ticketTypeId
""")
    BigDecimal calculateTicket(
            UUID eventId,
            UUID ticketTypeId,
            int quantity
    );
}
