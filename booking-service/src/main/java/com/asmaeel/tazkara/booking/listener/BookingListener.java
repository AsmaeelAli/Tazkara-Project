package com.asmaeel.tazkara.booking.listener;

import com.asmaeel.tazkara.booking.model.dto.BookingChangedEventDto;
import com.asmaeel.tazkara.booking.publisher.PublishAvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookingListener {

    private static final Logger log = LoggerFactory.getLogger(BookingListener.class);

    private final PublishAvailabilityService publishAvailabilityService;

    public BookingListener(PublishAvailabilityService publishAvailabilityService) {
        this.publishAvailabilityService = publishAvailabilityService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingChanged(BookingChangedEventDto event) {
        try {
            publishAvailabilityService.execute(event);
        } catch (Exception ex) {
            log.warn("Could not publish availability for event {}", event.eventId(), ex);
        }
    }
}
