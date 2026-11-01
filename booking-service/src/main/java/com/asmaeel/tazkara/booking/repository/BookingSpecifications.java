package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingSpecifications {

    public static Specification<BookingEntity> myBookings(
            UUID attendeeId,
            BookingStatus status
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("attendeeId"), attendeeId)
            );

            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status"), status)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
