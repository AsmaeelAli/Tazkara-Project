package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.model.dto.PublicEventSummaryResponseDto;
import com.asmaeel.tazkara.event.model.dto.PublicTicketTypeResponseDto;
import com.asmaeel.tazkara.event.model.entity.EventBookingSummaryEntity;
import com.asmaeel.tazkara.event.model.entity.EventEntity;
import com.asmaeel.tazkara.event.model.entity.TicketTypeEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PublicEventMapper {

    public PublicEventSummaryResponseDto toDto(EventEntity event,
                                               List<TicketTypeEntity> ticketTypes,
                                               List<EventBookingSummaryEntity> summaries) {

        Map<UUID, EventBookingSummaryEntity> summaryMap =
                summaries.stream()
                        .collect(Collectors.toMap(
                                EventBookingSummaryEntity::getTicketTypeId,
                                Function.identity()
                        ));

        List<PublicTicketTypeResponseDto> tickets = ticketTypes.stream()
                .map(ticketType -> {
                    EventBookingSummaryEntity summary =
                            summaryMap.get(ticketType.getId());

                    return new PublicTicketTypeResponseDto(
                            ticketType.getId(),
                            ticketType.getName(),
                            ticketType.getPrice(),
                            summary != null ? summary.getAvailable() : ticketType.getQuantity(),
                            summary != null && summary.getAvailable() == 0
                    );
                })
                .toList();

        return new PublicEventSummaryResponseDto(
                event.getId(),
                event.getTitle(),
                event.getCity(),
                event.getVenue(),
                event.getStartDateTime(),
                tickets
        );
    }
}
