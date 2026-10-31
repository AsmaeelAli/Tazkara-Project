package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.dto.ItemRequestDto;
import com.asmaeel.tazkara.booking.model.dto.ItemResponseDto;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.PublishedTicketTypeEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BookingResponseMapper {

    public BookingResponseDto toResponse(
            BookingEntity booking,
            List<PublishedTicketTypeEntity> types,
            List<ItemRequestDto> requestedItems) {

        List<ItemResponseDto> items = types.stream()
                .map(type -> {
                    ItemRequestDto item = requestedItems.stream()
                            .filter(i -> i.ticketTypeId().equals(type.getId()))
                            .findFirst()
                            .orElseThrow();

                    BigDecimal lineTotal = type.getPrice()
                            .multiply(BigDecimal.valueOf(item.quantity()));

                    return new ItemResponseDto(
                            type.getName(),
                            item.quantity(),
                            type.getPrice(),
                            lineTotal
                    );
                })
                .toList();

        return new BookingResponseDto(
                booking.getReference(),
                booking.getStatus(),
                booking.getEventId(),
                booking.getEventTitle(),
                booking.getEventStartTime(),
                booking.getTotalAmount(),
                items
        );
    }
}
