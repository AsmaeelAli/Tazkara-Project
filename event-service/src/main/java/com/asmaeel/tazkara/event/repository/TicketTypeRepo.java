package com.asmaeel.tazkara.event.repository;

import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketTypeRepo extends JpaRepository<TicketTypeEntity, UUID>,
        JpaSpecificationExecutor<TicketTypeEntity> {

    boolean existsByEventIdAndName(UUID eventId,String name);

    @Query("""
        SELECT CASE WHEN COUNT(t) < 5 THEN true ELSE false END
        FROM TicketTypeEntity t
        WHERE t.eventId = :eventId
    """)
    boolean hasAvailableTicketTypeSlots(@Param("eventId") UUID eventId);
}
