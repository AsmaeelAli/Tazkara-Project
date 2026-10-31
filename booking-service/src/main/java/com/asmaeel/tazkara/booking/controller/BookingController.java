package com.asmaeel.tazkara.booking.controller;

import com.asmaeel.tazkara.booking.model.dto.BookingRequestDto;
import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.dto.PaymentRequestDto;
import com.asmaeel.tazkara.booking.model.dto.PaymentResponseDto;
import com.asmaeel.tazkara.booking.usecase.PayBookingService;
import com.asmaeel.tazkara.booking.usecase.PlaceBookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final PlaceBookingService placeBookingService;
    private final PayBookingService payBookingService;

    public BookingController(PlaceBookingService placeBookingService,
                             PayBookingService payBookingService) {
        this.placeBookingService = placeBookingService;
        this.payBookingService = payBookingService;
    }

    @PostMapping
    public BookingResponseDto PlaceBooking(@RequestBody @Valid BookingRequestDto request) {
        return placeBookingService.execute(request);
    }

    @PostMapping("/{reference}/payment")
    public PaymentResponseDto pay(@PathVariable String reference,
                                  @RequestBody @Valid PaymentRequestDto request) {
        return payBookingService.execute(reference, request);
    }

}
