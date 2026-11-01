package com.asmaeel.tazkara.booking.factory;

import org.springframework.stereotype.Component;

@Component
public class NotificationChannelFactory {

    private final EmailChannel emailChannel;
    private final SmsChannel smsChannel;

    public NotificationChannelFactory(EmailChannel emailChannel,
                                      SmsChannel smsChannel) {
        this.emailChannel = emailChannel;
        this.smsChannel = smsChannel;
    }

    public NotificationChannel get(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailChannel;
            case SMS -> smsChannel;
        };
    }
}
