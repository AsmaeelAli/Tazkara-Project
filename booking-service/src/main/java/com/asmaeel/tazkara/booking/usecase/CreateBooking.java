package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.BookingRequestDto;
import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import org.springframework.stereotype.Service;

@Service
public class CreateBooking {
    private final BookingRepo bookingRepo;
    private final TicketRepo ticketRepo;

    public CreateBooking(BookingRepo bookingRepo, TicketRepo ticketRepo) {
        this.bookingRepo = bookingRepo;
        this.ticketRepo = ticketRepo;
    }

    public BookingResponseDto execute(BookingRequestDto request) {



        return null;
    }
}
