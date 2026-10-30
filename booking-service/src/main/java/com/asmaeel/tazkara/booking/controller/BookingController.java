package com.asmaeel.tazkara.booking.controller;

import com.asmaeel.tazkara.booking.model.dto.BookingRequestDto;
import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.usecase.PlaceBookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final PlaceBookingService placeBookingService;

    public BookingController(PlaceBookingService placeBookingService) {
        this.placeBookingService = placeBookingService;
    }

    @PostMapping
    public BookingResponseDto PlaceBooking(@RequestBody @Valid BookingRequestDto request) {
        return placeBookingService.execute(request);
    }
}
