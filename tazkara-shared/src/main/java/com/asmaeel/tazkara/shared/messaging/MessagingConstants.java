package com.asmaeel.tazkara.shared.messaging;

public final class MessagingConstants {

    public static final String EXCHANGE = "tazkara.events";

    public static final String EVENT_PUBLISHED_KEY = "event.published";
    public static final String EVENT_CANCELLED_KEY = "event.cancelled";
    public static final String AVAILABILITY_CHANGED_KEY = "booking.availability.changed";

    private MessagingConstants() {
    }
}
