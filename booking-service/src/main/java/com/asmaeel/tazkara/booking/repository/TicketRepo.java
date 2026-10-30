package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.model.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepo extends JpaRepository<TicketEntity, UUID>{
    boolean existsByEventId(UUID eventId);
}
