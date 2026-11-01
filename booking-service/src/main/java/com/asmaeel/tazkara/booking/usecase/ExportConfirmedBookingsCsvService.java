package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.eventclient.EventClient;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.BookingItemEntity;
import com.asmaeel.tazkara.booking.repository.BookingItemRepo;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import com.asmaeel.tazkara.shared.exception.ForbiddenException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportConfirmedBookingsCsvService {

    private static final int PAGE_SIZE = 500;
    private static final String NEW_LINE = "\r\n";

    private final BookingRepo bookingRepo;
    private final BookingItemRepo bookingItemRepo;
    private final EventClient eventClient;

    public ExportConfirmedBookingsCsvService(BookingRepo bookingRepo,
                                             BookingItemRepo bookingItemRepo,
                                             EventClient eventClient) {
        this.bookingRepo = bookingRepo;
        this.bookingItemRepo = bookingItemRepo;
        this.eventClient = eventClient;
    }

    @Transactional(readOnly = true)
    public byte[] execute(UUID eventId) {
        UUID currentUserId = UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());

        if (!currentUserId.equals(eventClient.getOwnerId(eventId))) {
            throw new ForbiddenException("You do not own this event");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8))) {

            writer.write("reference,attendee_email,ticket_type,quantity,unit_price,line_total,confirmed_at");
            writer.write(NEW_LINE);

            int page = 0;
            Page<BookingEntity> result;
            do {
                result = bookingRepo.findAllByEventIdAndStatus(
                        eventId, BookingStatus.CONFIRMED,
                        PageRequest.of(page++, PAGE_SIZE, Sort.by("createdAt", "id")));

                Map<UUID, BookingEntity> bookingsById = result.getContent().stream()
                        .collect(Collectors.toMap(BookingEntity::getId, Function.identity()));

                if (!bookingsById.isEmpty()) {
                    List<BookingItemEntity> items =
                            bookingItemRepo.findAllByBookingIdIn(new ArrayList<>(bookingsById.keySet()));
                    for (BookingItemEntity item : items) {
                        writeRow(writer, bookingsById.get(item.getBookingId()), item);
                    }
                }
            } while (result.hasNext());

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate CSV", e);
        }
        return out.toByteArray();
    }

    private void writeRow(BufferedWriter writer, BookingEntity booking, BookingItemEntity item) throws IOException {
        writer.write(String.join(",",
                escape(booking.getReference()),
                escape(booking.getEmail()),
                escape(item.getName()),
                String.valueOf(item.getQuantity()),
                item.getPrice().toPlainString(),
                item.getLineTotal().toPlainString(),
                booking.getConfirmedAt().toString()));
        writer.write(NEW_LINE);
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
