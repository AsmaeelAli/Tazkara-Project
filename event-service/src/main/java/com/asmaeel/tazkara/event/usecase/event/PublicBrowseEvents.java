package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.model.dto.TicketResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PublicBrowseEvents {
//
//    private static final int MAX_SIZE = 50;
//    private final EventRepo eventRepo;
//    private final TicketTypeRepo ticketRepo;
//
//    public PublicBrowseEvents(EventRepo eventRepo, TicketTypeRepo ticketRepo) {
//        this.eventRepo = eventRepo;
//        this.ticketRepo = ticketRepo;
//    }
//
//    @Transactional(readOnly = true)
//    public PageResponse<EventResponseDto> execute(String city, Instant from, Instant to,
//                                               String keyword, int page, int size) {
//
//        Specification<EventEntity> spec = visible();
//
//        if (city != null && !city.isBlank()) {
//            spec = spec.and((root, q, cb) ->
//                    cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase()));
//        }
//        if (keyword != null && !keyword.isBlank()) {
//            spec = spec.and((root, q, cb) ->
//                    cb.like(cb.lower(root.get("title")), "%" + keyword.trim().toLowerCase() + "%"));
//        }
//        if (from != null) {
//            spec = spec.and((root, q, cb)
//                    -> cb.greaterThanOrEqualTo(root.get("startDateTime"), from));
//        }
//
//        if (to != null) {
//            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("startDateTime"), to));
//        }
//
//        Pageable pageable = PageRequest.of(Math.max(page, 0),
//                Math.min(Math.max(size, 1), MAX_SIZE),
//                Sort.by("startDateTime").ascending());
//
//        Page<EventEntity> result = eventRepo.findAll(spec, pageable);   // الاستعلام الأول
//
//        List<UUID> ids = result.getContent().stream().map(EventEntity::getId).toList();
//        Map<UUID, List<TicketResponseDto>> tickets = ids.isEmpty() ? Map.of()
//                : ticketRepo.findByEventIdIn(ids).stream()               // الاستعلام الثاني
//                .collect(Collectors.groupingBy(TicketTypeEntity::getEventId,
//                        Collectors.mapping(t -> new TicketResponseDto(
//                                t.getId(), t.getEventId(), t.getName(),
//                                t.getPrice(), t.getQuantity()), Collectors.toList())));
//
//        return PageResponse.from(result.map(e -> toDto(e, tickets.getOrDefault(e.getId(), List.of()))));
//    }
//
//    @Transactional(readOnly = true)
//    public EventResponseDto details(UUID eventId) {
//        EventEntity event = eventRepo.findOne(visible().and((r, q, cb) -> cb.equal(r.get("id"), eventId)))
//                .orElseThrow(() -> new NoSuchElementException("Event not found"));
//
//        List<TicketResponseDto> tickets = ticketRepo.findByEventId(eventId).stream()
//                .map(t -> new TicketResponseDto(t.getId(), t.getEventId(), t.getName(),
//                        t.getPrice(), t.getQuantity()))
//                .toList();
//        return toDto(event, tickets);
//    }
//
//
//    private Specification<EventEntity> visible() {
//        Instant now = Instant.now();
//        return (root, q, cb)
//                -> cb.and(
//                cb.equal(root.get("status"), EventStatus.PUBLISHED),
//                cb.greaterThan(root.get("startDateTime"), now));
//    }
//
//    private EventResponseDto toDto(EventEntity e, List<TicketResponseDto> tickets) {
//        return new EventResponseDto(e.getId(), e.getTitle(), e.getDescription(), e.getVenue(),
//                e.getCity(), e.getStartDateTime(), e.getStatus(), tickets);
//    }
}
