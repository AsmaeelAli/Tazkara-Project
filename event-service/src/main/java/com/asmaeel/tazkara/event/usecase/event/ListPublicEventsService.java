package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.model.dto.PublicEventSummaryResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.repository.EventSpecifications;
import com.asmaeel.tazkara.event.repository.TicketTypeRepo;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ListPublicEventsService {

    private final EventRepo eventRepo;
    private final TicketTypeRepo ticketRepo;

    public ListPublicEventsService(
            EventRepo eventRepo,
            TicketTypeRepo ticketRepo
    ) {
        this.eventRepo = eventRepo;
        this.ticketRepo = ticketRepo;
    }

    public PageResponse<PublicEventSummaryResponseDto> execute(int page, int size, String city, Instant from, Instant to, String keyword) {

        Instant now = Instant.now();

        Specification<EventEntity> specification =
                EventSpecifications.publicUpcomingEvents(now, city, from, to, keyword);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startDateTime")
        );

        Page<EventEntity> events = eventRepo.findAll(specification, pageable);

        Page<PublicEventSummaryResponseDto> mappedEvents =
                events.map(event -> new PublicEventSummaryResponseDto(
                        event.getId(),
                        event.getTitle(),
                        event.getCity(),
                        event.getVenue(),
                        event.getStartDateTime(),
                        List.of()
                ));

        return PageResponse.from(mappedEvents);
    }
}



