package com.asmaeel.tazkara.booking.publisher;

import com.asmaeel.tazkara.booking.common.TicketStatus;
import com.asmaeel.tazkara.booking.model.dto.BookingChangedEventDto;
import com.asmaeel.tazkara.booking.model.dto.TicketStatusCount;
import com.asmaeel.tazkara.booking.repository.PublishedEventRepo;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import com.asmaeel.tazkara.shared.dto.AvailabilityChangedMessageDto;
import com.asmaeel.tazkara.shared.dto.TicketTypeCountsDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PublishAvailabilityService {

    private final TicketRepo ticketRepo;
    private final RabbitMqProducer rabbitMqProducer;
    private final PublishedEventRepo publishedEventRepo;

    public PublishAvailabilityService(RabbitMqProducer rabbitMqProducer,
                                      TicketRepo ticketRepo,
                                      PublishedEventRepo publishedEventRepo) {
        this.rabbitMqProducer = rabbitMqProducer;
        this.ticketRepo = ticketRepo;
        this.publishedEventRepo = publishedEventRepo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void execute(BookingChangedEventDto update) {

        UUID eventId = update.eventId();

        if (!publishedEventRepo.existsById(eventId)) {
            return;
        }

        List<TicketStatusCount> rows = ticketRepo.countByTypeAndStatus(eventId);

        List<TicketTypeCountsDto> ticketTypes = rows.stream()
                .collect(Collectors.groupingBy(TicketStatusCount::ticketTypeId))
                .entrySet().stream()
                .map(entry -> new TicketTypeCountsDto(
                        entry.getKey(),
                        countOf(entry.getValue(), TicketStatus.FREE),
                        countOf(entry.getValue(), TicketStatus.HELD),
                        countOf(entry.getValue(), TicketStatus.SOLD)))
                .toList();

        rabbitMqProducer.publishAvailabilityChanged(
                new AvailabilityChangedMessageDto(eventId, Instant.now(), ticketTypes));
    }

    private int countOf(List<TicketStatusCount> rows, TicketStatus status) {
        return rows.stream()
                .filter(row -> row.status() == status)
                .mapToInt(row -> (int) row.count())
                .sum();
    }
}
