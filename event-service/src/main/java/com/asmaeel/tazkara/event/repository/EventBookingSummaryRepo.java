package com.asmaeel.tazkara.event.repository;

import com.asmaeel.tazkara.event.model.entity.EventBookingSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventBookingSummaryRepo extends JpaRepository<EventBookingSummaryEntity, UUID> {
    List<EventBookingSummaryEntity> findAllByEventId(UUID eventId);
    Optional<EventBookingSummaryEntity> findByTicketTypeId(UUID ticketTypeId);
}
