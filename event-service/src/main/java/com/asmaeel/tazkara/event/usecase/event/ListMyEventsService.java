package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.common.EventStatus;
import com.asmaeel.tazkara.event.model.dto.EventResponseDto;
import com.asmaeel.tazkara.event.model.dto.TicketTypeResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListMyEventsService {
    private static final int MAX_SIZE = 50;
    private final EventRepo eventRepo;
    private final TicketTypeRepo ticketTypeRepo;

    public ListMyEventsService(EventRepo eventRepo , TicketTypeRepo ticketTypeRepo) {
        this.eventRepo = eventRepo;
        this.ticketTypeRepo = ticketTypeRepo;
    }

    @Transactional(readOnly = true)
    public PageResponse<EventResponseDto> execute(EventStatus status, int page, int size) {
        UUID ownerId = UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());

        Pageable pageable = PageRequest.of(Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_SIZE),
                Sort.by("startDateTime").ascending());

        Page<EventEntity> result = (status == null)
                ? eventRepo.findByOwnerId(ownerId, pageable)
                : eventRepo.findByOwnerIdAndStatus(ownerId, status, pageable);

        List<UUID> ids = result.getContent().stream().map(EventEntity::getId).toList();


        Map<UUID, List<TicketTypeResponseDto>> ticketsByEvent = ids.isEmpty()
                ? Map.of()
                : ticketTypeRepo.findByEventIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        TicketTypeEntity::getEventId,
                        Collectors.mapping(t -> new TicketTypeResponseDto(
                                        t.getName(),
                                        t.getPrice(),
                                        t.getQuantity()),
                                Collectors.toList())));

        return PageResponse.from(result.map(e -> new EventResponseDto(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getVenue(),
                e.getCity(),
                e.getStartDateTime(),
                e.getStatus(),
                ticketsByEvent.getOrDefault(e.getId(),
                        List.of()))));
    }
}
