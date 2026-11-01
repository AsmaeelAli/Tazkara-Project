package com.asmaeel.tazkara.booking.factory;

import com.asmaeel.tazkara.booking.model.dto.BookingSenderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);

    @Override
    public void send(BookingSenderDto data) {
        String subject = "Booking Status Update - " + data.eventTitle();

        String message = """
            Hello,

            Your booking status for the event "%s" has been updated.

            Booking Reference: %s
            New Status: %s

            Thank you for using Tazkara!

            Best regards,
            Tazkara Team
            """.formatted(
                data.eventTitle(),
                data.reference(),
                data.newStatus()
        );

        log.info("Email to={} subject={} message={}",
                data.attendeeEmail(), subject, message);
    }
}