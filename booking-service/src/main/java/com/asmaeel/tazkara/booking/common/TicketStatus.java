package com.asmaeel.tazkara.booking.common;

public enum TicketStatus {
    FREE,
    HELD,
    SOLD;

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case FREE -> target == HELD;
            case HELD -> target == SOLD || target == FREE;
            case SOLD -> target == FREE;
        };
    }
}
