package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.model.entity.BookingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingItemRepo extends JpaRepository<BookingItemEntity, UUID>, JpaSpecificationExecutor<BookingItemEntity> {
    List<BookingItemEntity> findAllByBookingId(UUID bookingId);
    List<BookingItemEntity> findAllByBookingIdIn(List<UUID> bookingIds);
}
