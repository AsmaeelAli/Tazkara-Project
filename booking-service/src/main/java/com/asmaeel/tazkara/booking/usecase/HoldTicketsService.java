package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.model.dto.ItemRequestDto;
import com.asmaeel.tazkara.booking.model.entity.TicketEntity;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import com.asmaeel.tazkara.shared.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HoldTicketsService {

    private final TicketRepo ticketRepo;

    public HoldTicketsService(TicketRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    /**
     *
     * Transactional(propagation = Propagation.MANDATORY)

     *  هي نوع ترانزاكشن بحدد كيف تستعمل الميثود

     *  في الوضع الطبيعي بدون Propagation اذا مافي وحدة قبلي بعمل جديدة

     *  اما في حالتنا هاي نوعهاMANDATORY ويعني انت ملزم تحط الميثود داخل ترانزاكشن بدونها برمي اكسبشن

     *  السبب هو لانه عملية حجز التذاكر ضمن عملية الحجز فا ما بصير تكون كل عملية مستقلة عن الثانية
     * */

    @Transactional(propagation = Propagation.MANDATORY)
    public void execute(UUID bookingId, UUID eventId, List<ItemRequestDto> items) {

        for (ItemRequestDto item : items) {

            // 5. Select available tickets
            List<TicketEntity> tickets = ticketRepo.lockFreeTickets(
                    eventId, item.ticketTypeId(), item.quantity());

            if (tickets.size() < item.quantity()) {
                throw new ConflictException("Not enough tickets available");
            }

            // 6. Change tickets status to HELD and Associate tickets with booking-id
            tickets.forEach(t -> t.hold(bookingId));
        }
    }
}
