package com.asmaeel.tazkara.booking.factory;

import com.asmaeel.tazkara.booking.model.dto.BookingSenderDto;

public interface NotificationChannel {
    void send(BookingSenderDto data);
}
