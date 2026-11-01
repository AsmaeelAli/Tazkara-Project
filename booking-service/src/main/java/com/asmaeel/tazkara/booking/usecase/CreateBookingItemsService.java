package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.ItemRequestDto;
import com.asmaeel.tazkara.booking.model.entity.BookingItemEntity;
import com.asmaeel.tazkara.booking.model.entity.PublishedTicketTypeEntity;
import com.asmaeel.tazkara.booking.repository.BookingItemRepo;
import com.asmaeel.tazkara.booking.repository.PublishedTicketTypeRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CreateBookingItemsService {

    private final PublishedTicketTypeRepo publishedTicketTypeRepo;
    private final BookingItemRepo bookingItemRepo;

    public CreateBookingItemsService(PublishedTicketTypeRepo publishedTicketTypeRepo, BookingItemRepo bookingItemRepo) {
        this.publishedTicketTypeRepo = publishedTicketTypeRepo;
        this.bookingItemRepo = bookingItemRepo;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<BookingItemEntity> execute(
            UUID bookingId,
            List<ItemRequestDto> items
    ) {

        List<UUID> typeIds = items.stream()
                .map(ItemRequestDto::ticketTypeId)
                .toList();

        List<PublishedTicketTypeEntity> types =
                publishedTicketTypeRepo.findAllById(typeIds);

        List<BookingItemEntity> bookingItems = items.stream()
                .map(item -> {
                    PublishedTicketTypeEntity type = types.stream()
                            .filter(t -> t.getId().equals(item.ticketTypeId()))
                            .findFirst()
                            .orElseThrow();

                    BigDecimal lineTotal = type.getPrice()
                            .multiply(BigDecimal.valueOf(item.quantity()));

                    return new BookingItemEntity(
                            bookingId,
                            type.getId(),
                            type.getName(),
                            item.quantity(),
                            type.getPrice(),
                            lineTotal
                    );
                })
                .toList();

        return bookingItemRepo.saveAll(bookingItems);
    }
}
