package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.common.TicketStatus;
import com.asmaeel.tazkara.booking.model.dto.TicketDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketBatchRepo {

    private final JdbcTemplate jdbcTemplate;

    public TicketBatchRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    public void createTickets(Integer batchSize , List<TicketDto> tickets) {

        String sql = """
                INSERT INTO tickets (id, event_id,ticket_type_id,status)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, tickets, batchSize, (ps, ticket) -> {
            ps.setObject(1, ticket.id());
            ps.setObject(2, ticket.eventId());
            ps.setObject(3, ticket.ticketTypeId());
            ps.setString(4, TicketStatus.FREE.name());
        });
    }
}
