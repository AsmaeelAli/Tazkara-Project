package com.asmaeel.tazkara.event.repository;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepo extends JpaRepository<EventEntity, UUID> , JpaSpecificationExecutor<EventEntity> {

    Page<EventEntity> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<EventEntity> findByOwnerIdAndStatus(UUID ownerId, EventStatus status, Pageable pageable);

    boolean existsByOwnerIdAndTitle(UUID ownerId,String title);

    @Query("""
    SELECT CASE WHEN COUNT(e) < 10 THEN true ELSE false END
    FROM EventEntity e
    WHERE e.ownerId = :ownerId
""")
    boolean hasAvailableEventSlots(@Param("ownerId") UUID organizerId);
}
