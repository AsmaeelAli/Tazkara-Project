package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.dto.ItemResponseDto;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.BookingItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingResponseMapper {

    public BookingResponseDto toResponse(
            BookingEntity booking,
            List<BookingItemEntity> bookingItems) {

        List<ItemResponseDto> items = bookingItems.stream()
                .map(item -> new ItemResponseDto(
                        item.getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getLineTotal()
                ))
                .toList();

        return new BookingResponseDto(
                booking.getReference(),
                booking.getStatus(),
                booking.getEventId(),
                booking.getEventTitle(),
                booking.getEventStartTime(),
                booking.getTotalAmount(),
                items,
                booking.getHoldExpiresAt(),
                booking.getCancellationReason()
        );
    }
}