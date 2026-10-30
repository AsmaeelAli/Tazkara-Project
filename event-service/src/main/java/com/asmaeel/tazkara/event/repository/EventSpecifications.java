package com.asmaeel.tazkara.event.repository;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EventSpecifications {

    public static Specification<EventEntity> publicUpcomingEvents(Instant now, String city, Instant from, Instant to, String keyword) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("status"), EventStatus.PUBLISHED)
            );

            predicates.add(
                    cb.greaterThan(root.get("startDateTime"), now)
            );


            if (city != null && !city.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("city")),
                                 city.trim().toLowerCase() + "%"
                        )
                );
            }


            if (from != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("startDateTime"), from)
                );
            }


            if (to != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("startDateTime"), to)
                );
            }


            if (keyword != null && !keyword.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + keyword.trim().toLowerCase() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
