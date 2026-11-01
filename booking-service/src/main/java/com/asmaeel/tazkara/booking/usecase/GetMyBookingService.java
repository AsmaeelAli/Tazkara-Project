package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.BookingItemEntity;
import com.asmaeel.tazkara.booking.repository.BookingItemRepo;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class GetMyBookingService {

    private final BookingRepo bookingRepo;
    private final BookingItemRepo bookingItemRepo;
    private final BookingResponseMapper bookingResponseMapper;

    public GetMyBookingService(
            BookingRepo bookingRepo,
            BookingItemRepo bookingItemRepo,
            BookingResponseMapper bookingResponseMapper) {

        this.bookingRepo = bookingRepo;
        this.bookingItemRepo = bookingItemRepo;
        this.bookingResponseMapper = bookingResponseMapper;
    }

    @Transactional(readOnly = true)
    public BookingResponseDto execute(String reference) {

        UUID attendeeId = UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );

        BookingEntity booking = bookingRepo
                .findByReferenceAndAttendeeId(reference, attendeeId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        List<BookingItemEntity> bookingItems =
                bookingItemRepo.findAllByBookingId(booking.getId());

        return bookingResponseMapper.toResponse(booking, bookingItems);
    }
}