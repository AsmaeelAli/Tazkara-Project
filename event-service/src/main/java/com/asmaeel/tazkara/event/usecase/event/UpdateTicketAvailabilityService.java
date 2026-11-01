package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.model.entity.EventBookingSummaryEntity;
import com.asmaeel.tazkara.event.repository.EventBookingSummaryRepo;
import com.asmaeel.tazkara.shared.dto.AvailabilityChangedMessageDto;
import com.asmaeel.tazkara.shared.dto.TicketTypeCountsDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UpdateTicketAvailabilityService {

    private final EventBookingSummaryRepo eventBookingSummaryRepo;

    public UpdateTicketAvailabilityService(EventBookingSummaryRepo eventBookingSummaryRepo) {
        this.eventBookingSummaryRepo = eventBookingSummaryRepo;
    }

    @Transactional
    public void execute(AvailabilityChangedMessageDto message) {

        for (TicketTypeCountsDto counts : message.ticketTypes()) {
            EventBookingSummaryEntity summary = eventBookingSummaryRepo
                    .findByTicketTypeId(counts.ticketTypeId())
                    .orElse(null);

            if (summary == null) {
                eventBookingSummaryRepo.save(new EventBookingSummaryEntity(
                        message.eventId(),
                        counts.ticketTypeId(),
                        counts.available(),
                        counts.held(),
                        counts.sold(),
                        message.sentAt()));
            } else {
                summary.updateCounts(counts.available(), counts.held(),
                        counts.sold(), message.sentAt());
            }
        }

    }
}
