package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<BookingEntity, UUID> , JpaSpecificationExecutor<BookingEntity> {
}
