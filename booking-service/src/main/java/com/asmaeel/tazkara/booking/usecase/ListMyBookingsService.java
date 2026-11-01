package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.BookingItemEntity;
import com.asmaeel.tazkara.booking.repository.BookingItemRepo;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import com.asmaeel.tazkara.booking.repository.BookingSpecifications;
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
public class ListMyBookingsService {

    private final BookingRepo bookingRepo;
    private final BookingItemRepo bookingItemRepo;
    private final BookingResponseMapper bookingResponseMapper;

    public ListMyBookingsService(
            BookingRepo bookingRepo,
            BookingItemRepo bookingItemRepo,
            BookingResponseMapper bookingResponseMapper) {

        this.bookingRepo = bookingRepo;
        this.bookingItemRepo = bookingItemRepo;
        this.bookingResponseMapper = bookingResponseMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDto> execute(
            BookingStatus status, Integer page, Integer size) {

        UUID attendeeId = UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );

        // 1. Fetch paginated bookings
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<BookingEntity> bookings = bookingRepo.findAll(
                BookingSpecifications.myBookings(attendeeId, status),
                pageable
        );

        // 2. Extract booking IDs
        List<UUID> bookingIds = bookings.getContent()
                .stream()
                .map(BookingEntity::getId)
                .toList();

        // 3. Fetch all booking items in one query
        List<BookingItemEntity> bookingItems = bookingIds.isEmpty()
                ? List.of()
                : bookingItemRepo.findAllByBookingIdIn(bookingIds);

        // 4. Group items by booking ID
        Map<UUID, List<BookingItemEntity>> itemsByBooking = bookingItems
                .stream()
                .collect(Collectors.groupingBy(BookingItemEntity::getBookingId));

        // 5. Map bookings to response DTOs
        Page<BookingResponseDto> response = bookings.map(booking ->
                bookingResponseMapper.toResponse(
                        booking,
                        itemsByBooking.getOrDefault(booking.getId(), List.of())
                )
        );

        return PageResponse.from(response);
    }
}
