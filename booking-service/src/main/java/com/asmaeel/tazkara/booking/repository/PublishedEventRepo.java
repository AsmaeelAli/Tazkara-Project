package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.model.entity.PublishedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PublishedEventRepo extends JpaRepository<PublishedEventEntity, UUID> {
}
