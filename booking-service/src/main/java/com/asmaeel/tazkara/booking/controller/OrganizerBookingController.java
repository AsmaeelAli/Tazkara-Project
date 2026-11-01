package com.asmaeel.tazkara.booking.controller;

import com.asmaeel.tazkara.booking.usecase.ExportConfirmedBookingsCsvService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizer/events/{eventId}")
public class OrganizerBookingController {

    private final ExportConfirmedBookingsCsvService exportCsvService;

    public OrganizerBookingController(ExportConfirmedBookingsCsvService exportCsvService) {
        this.exportCsvService = exportCsvService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@PathVariable UUID eventId) {
        byte[] csv = exportCsvService.execute(eventId);
        String fileName = "bookings-" + eventId + "-" + LocalDate.now(ZoneOffset.UTC) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv);
    }
}
