package com.asmaeel.tazkara.booking.usecase;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import com.asmaeel.tazkara.booking.model.entity.TicketEntity;
import com.asmaeel.tazkara.booking.repository.BookingRepo;
import com.asmaeel.tazkara.booking.repository.TicketRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ExpireBookingsService {

    private static final Logger log = LoggerFactory.getLogger(ExpireBookingsService.class);

    private final BookingRepo bookingRepo;
    private final TicketRepo ticketRepo;
    private final TransactionTemplate tx;

    public ExpireBookingsService(BookingRepo bookingRepo,
                                 TicketRepo ticketRepo,
                                 PlatformTransactionManager transactionManager) {
        this.bookingRepo = bookingRepo;
        this.ticketRepo = ticketRepo;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @Scheduled(fixedDelayString = "${booking.expiry.interval}")
    public void execute() {

        Instant now = Instant.now();

        List<UUID> ids = bookingRepo.findExpiredIds(
                BookingStatus.PENDING_PAYMENT, now, PageRequest.of(0, 200));

        for (UUID id : ids) {
            try {
                // transaction مستقلة لكل حجز
                tx.executeWithoutResult(status -> expireOne(id, now));
            } catch (ObjectOptimisticLockingFailureException e) {
                log.info("Booking {} was updated by another process, skipped", id);
            } catch (RuntimeException e) {
                log.error("Failed to expire booking {}: {}", id, e.getMessage());
            }
        }

        if (!ids.isEmpty()) {
            log.info("Expiry job processed {} bookings", ids.size());
        }
    }

    private void expireOne(UUID id, Instant now) {

        BookingEntity booking = bookingRepo.findById(id).orElse(null);

        if (booking == null || booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            return;
        }

        if (!booking.expire(now)) {
            log.warn("Skipping expiration for booking {}. Current status: {}",
                    booking.getId(), booking.getStatus());
            return;
        }

        ticketRepo.findAllByBookingId(id).forEach(TicketEntity::release);
    }
}
