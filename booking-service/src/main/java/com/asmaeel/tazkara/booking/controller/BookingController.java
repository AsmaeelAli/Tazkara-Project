package com.asmaeel.tazkara.booking.controller;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.model.dto.BookingRequestDto;
import com.asmaeel.tazkara.booking.model.dto.BookingResponseDto;
import com.asmaeel.tazkara.booking.model.dto.PaymentRequestDto;
import com.asmaeel.tazkara.booking.model.dto.PaymentResponseDto;
import com.asmaeel.tazkara.booking.usecase.GetMyBookingService;
import com.asmaeel.tazkara.booking.usecase.ListMyBookingsService;
import com.asmaeel.tazkara.booking.usecase.PayBookingService;
import com.asmaeel.tazkara.booking.usecase.PlaceBookingService;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final PlaceBookingService placeBookingService;
    private final PayBookingService payBookingService;
    private final ListMyBookingsService listMyBookingsService;
    private final GetMyBookingService getMyBookingService;

    public BookingController(PlaceBookingService placeBookingService,
                             PayBookingService payBookingService,
                             ListMyBookingsService listMyBookingsService,
                             GetMyBookingService getMyBookingService) {

        this.placeBookingService = placeBookingService;
        this.payBookingService = payBookingService;
        this.listMyBookingsService = listMyBookingsService;
        this.getMyBookingService = getMyBookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto placeBooking(@RequestBody @Valid BookingRequestDto request) {
        return placeBookingService.execute(request);
    }

    @PostMapping("/{reference}/payment")
    public PaymentResponseDto pay(@PathVariable String reference,
                                  @RequestBody @Valid PaymentRequestDto request) {
        return payBookingService.execute(reference, request);
    }

    @PostMapping("/{reference}/cancel")
    public BookingResponseDto cancel(@PathVariable String reference) {
        return null;
    }

    @GetMapping
    public PageResponse<BookingResponseDto> myBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return listMyBookingsService.execute(status, Math.max(page, 0), Math.max(1, Math.min(size, 50)));
    }

    @GetMapping("/{reference}")
    public BookingResponseDto getByReference(@PathVariable String reference) {
        return getMyBookingService.execute(reference);
    }
}