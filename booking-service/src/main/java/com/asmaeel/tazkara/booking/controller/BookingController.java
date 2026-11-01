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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final PayBookingService payBookingService;
    private final PlaceBookingService placeBookingService;
    private final GetMyBookingService getMyBookingService;
    private final ListMyBookingsService listMyBookingsService;

    public BookingController(PlaceBookingService placeBookingService,
                             GetMyBookingService getMyBookingService,
                             ListMyBookingsService listMyBookingsService,
                             PayBookingService payBookingService) {

        this.payBookingService = payBookingService;
        this.placeBookingService = placeBookingService;
        this.getMyBookingService = getMyBookingService;
        this.listMyBookingsService = listMyBookingsService;
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

    @GetMapping()
    public PageResponse<BookingResponseDto> myBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        return listMyBookingsService.execute(status, page, Math.min(size, 50));
    }

    @GetMapping("/{reference}")
    public BookingResponseDto getBookingByReference(@PathVariable String reference) {
        return getMyBookingService.execute(reference);
    }

}
