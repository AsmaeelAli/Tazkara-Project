package com.asmaeel.tazkara.booking.factory;

import com.asmaeel.tazkara.booking.model.dto.BookingSenderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsChannel implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);

    @Override
    public void send(BookingSenderDto data) {
        String message = """
            Tazkara Notification:

            Your booking for "%s" has been updated.
            Reference: %s
            Status: %s

            Thank you for choosing Tazkara!

            Oops! Part of this message seems to be missing. ^-^!
            """.formatted(
                data.eventTitle(),
                data.reference(),
                data.newStatus()
        );

        log.info("SMS to={} message={}", data.attendeeEmail(), message);
    }
}
