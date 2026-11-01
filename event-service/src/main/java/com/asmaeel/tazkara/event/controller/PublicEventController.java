package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.model.dto.PublicEventSummaryResponseDto;
import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.event.usecase.event.ListPublicEventsService;
import com.asmaeel.tazkara.shared.dto.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/events/public")
public class PublicEventController {

    private final ListPublicEventsService listPublicEventsService;

    public PublicEventController(ListPublicEventsService listPublicEventsService) {
        this.listPublicEventsService = listPublicEventsService;
    }

    @GetMapping()
    public PageResponse<PublicEventSummaryResponseDto> listPublicEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String keyword
    ) {
        page = Math.max(page, 0);
        size = Math.max(1, Math.min(size, 50));

        return listPublicEventsService.execute(
                page, size, city, from, to, keyword
        );
    }
}
