package com.asmaeel.tazkara.event.listener;

import com.asmaeel.tazkara.event.usecase.event.UpdateTicketAvailabilityService;
import com.asmaeel.tazkara.shared.dto.AvailabilityChangedMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityChangedListener {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityChangedListener.class);

    private final UpdateTicketAvailabilityService updateTicketAvailabilityService;

    public AvailabilityChangedListener(UpdateTicketAvailabilityService updateTicketAvailabilityService) {
        this.updateTicketAvailabilityService = updateTicketAvailabilityService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.availability-changed}")
    public void onAvailabilityChanged(AvailabilityChangedMessageDto message) {
        log.info("Received availability change for event {}", message.eventId());
        updateTicketAvailabilityService.execute(message);
    }
}
