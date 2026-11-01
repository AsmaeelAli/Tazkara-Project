package com.asmaeel.tazkara.booking.listener;

import com.asmaeel.tazkara.booking.factory.NotificationChannelFactory;
import com.asmaeel.tazkara.booking.factory.NotificationType;
import com.asmaeel.tazkara.booking.model.dto.BookingChangedEventDto;
import com.asmaeel.tazkara.booking.model.dto.BookingSenderDto;
import com.asmaeel.tazkara.booking.publisher.PublishAvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookingListener {

    private static final Logger log = LoggerFactory.getLogger(BookingListener.class);

    private final PublishAvailabilityService publishAvailabilityService;
    private final NotificationChannelFactory notificationChannelFactory;

    public BookingListener(PublishAvailabilityService publishAvailabilityService,
                           NotificationChannelFactory notificationChannelFactory) {

        this.publishAvailabilityService = publishAvailabilityService;
        this.notificationChannelFactory = notificationChannelFactory;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingPayed(BookingChangedEventDto event) {
        try {
            publishAvailabilityService.execute(event);
            log.info("BookingChangedEvent received: {}", event.eventId());
        } catch (Exception ex) {
            log.warn("Could not publish availability for event {}", event.eventId(), ex);
        }
    }

    @EventListener
    public void sender(BookingSenderDto data) {
        notificationChannelFactory.get(NotificationType.SMS).send(data);
        notificationChannelFactory.get(NotificationType.EMAIL).send(data);
    }
}
