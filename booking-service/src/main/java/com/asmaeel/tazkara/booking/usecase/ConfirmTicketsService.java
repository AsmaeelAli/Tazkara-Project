package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.common.TicketStatus;
import com.asmaeel.tazkara.booking.model.entity.TicketEntity;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConfirmTicketsService {
    private final TicketRepo ticketRepo;
    public ConfirmTicketsService(TicketRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void execute(UUID bookingId) {
        List<TicketEntity> ticket = ticketRepo.findAllByBookingIdAndStatus(bookingId, TicketStatus.HELD);
        ticket.forEach(TicketEntity::sell);
    }
}
