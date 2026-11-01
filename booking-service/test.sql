WITH p AS (SELECT '6efe3d2f-1fe6-49af-a5f3-7f19f5bc97ea'::uuid AS eid),

     act AS (  -- active bookings of this event
         SELECT b.* FROM bookings b, p
         WHERE b.event_id = p.eid AND b.status IN ('PENDING_PAYMENT','CONFIRMED')
     ),

     checks AS (

         SELECT 1 AS n, 'max tickets for a single user (<= 6)' AS check_name,
                coalesce(max(s),0)::text AS value, coalesce(max(s),0) <= 6 AS ok
FROM (SELECT sum(quantity) s FROM act GROUP BY attendee_id) x

UNION ALL
SELECT 2, 'total tickets = published quantity',
       (SELECT count(*) FROM tickets, p WHERE event_id = p.eid)::text || ' of ' ||
                (SELECT coalesce(sum(quantity),0) FROM published_ticket_types, p WHERE event_id = p.eid)::text,
    (SELECT count(*) FROM tickets, p WHERE event_id = p.eid) =
    (SELECT coalesce(sum(quantity),0) FROM published_ticket_types, p WHERE event_id = p.eid)

UNION ALL
SELECT 3, 'ticket types whose ticket count differs from quantity (0)',
       count(*)::text, count(*) = 0
FROM published_ticket_types pt, p
WHERE pt.event_id = p.eid
  AND (SELECT count(*) FROM tickets t WHERE t.ticket_type_id = pt.id) <> pt.quantity

UNION ALL
SELECT 4, 'tickets of an unknown ticket type (0)',
       count(*)::text, count(*) = 0
FROM tickets t, p
WHERE t.event_id = p.eid
  AND NOT EXISTS (SELECT 1 FROM published_ticket_types pt
                  WHERE pt.id = t.ticket_type_id AND pt.event_id = t.event_id)

UNION ALL
SELECT 5, 'non-free tickets without a booking (0)',
       count(*)::text, count(*) = 0
FROM tickets, p WHERE event_id = p.eid AND status <> 'FREE' AND booking_id IS NULL

UNION ALL
SELECT 6, 'FREE tickets still linked to a booking (0)',
       count(*)::text, count(*) = 0
FROM tickets, p WHERE event_id = p.eid AND status = 'FREE' AND booking_id IS NOT NULL

UNION ALL
SELECT 7, 'HELD tickets whose booking is not PENDING_PAYMENT (0)',
       count(*)::text, count(*) = 0
FROM tickets t, p
WHERE t.event_id = p.eid AND t.status = 'HELD'
  AND NOT EXISTS (SELECT 1 FROM bookings b
                  WHERE b.id = t.booking_id AND b.status = 'PENDING_PAYMENT')

UNION ALL
SELECT 8, 'SOLD tickets whose booking is not CONFIRMED (0)',
       count(*)::text, count(*) = 0
FROM tickets t, p
WHERE t.event_id = p.eid AND t.status = 'SOLD'
  AND NOT EXISTS (SELECT 1 FROM bookings b
                  WHERE b.id = t.booking_id AND b.status = 'CONFIRMED')

UNION ALL
SELECT 9, 'sum of active booking quantities = non-free tickets',
       (SELECT coalesce(sum(quantity),0) FROM act)::text || ' vs ' ||
                (SELECT count(*) FROM tickets, p WHERE event_id = p.eid AND status <> 'FREE')::text,
    (SELECT coalesce(sum(quantity),0) FROM act) =
    (SELECT count(*) FROM tickets, p WHERE event_id = p.eid AND status <> 'FREE')

UNION ALL
SELECT 10, 'active bookings whose quantity differs from their ticket count (0)',
       count(*)::text, count(*) = 0
FROM act a
WHERE a.quantity <> (SELECT count(*) FROM tickets t WHERE t.booking_id = a.id)

UNION ALL
SELECT 11, 'bookings with quantity <= 0 (0)',
       count(*)::text, count(*) = 0
FROM bookings b, p WHERE b.event_id = p.eid AND b.quantity <= 0

UNION ALL
SELECT 12, 'active bookings whose total differs from the sum of ticket prices (0)',
       count(*)::text, count(*) = 0
FROM act a
WHERE a.total_amount <> (SELECT coalesce(sum(pt.price),0)
                         FROM tickets t JOIN published_ticket_types pt ON pt.id = t.ticket_type_id
                         WHERE t.booking_id = a.id)

UNION ALL
SELECT 13, 'duplicate booking references (0)',
       count(*)::text, count(*) = 0
FROM (SELECT reference FROM bookings GROUP BY reference HAVING count(*) > 1) d

UNION ALL
SELECT 14, 'booking status inconsistent with confirmed_at (0)',
       count(*)::text, count(*) = 0
FROM bookings b, p
WHERE b.event_id = p.eid
  AND ((b.status = 'CONFIRMED' AND b.confirmed_at IS NULL)
    OR (b.status = 'PENDING_PAYMENT' AND b.confirmed_at IS NOT NULL))

UNION ALL
SELECT 15, 'CANCELLED bookings without a reason (0)',
       count(*)::text, count(*) = 0
FROM bookings b, p
WHERE b.event_id = p.eid AND b.status = 'CANCELLED' AND b.cancellation_reason IS NULL

UNION ALL
SELECT 16, 'PENDING bookings overdue by more than 2 minutes (job is alive, 0)',
       count(*)::text, count(*) = 0
FROM bookings b, p
WHERE b.event_id = p.eid AND b.status = 'PENDING_PAYMENT'
  AND b.hold_expires_at < now() - interval '2 minutes'

UNION ALL
SELECT 17, 'bookings with null version (0)',
       count(*)::text, count(*) = 0
FROM bookings b, p WHERE b.event_id = p.eid AND b.version IS NULL

UNION ALL
SELECT 18, 'bookings of an event that is not in published_events (0)',
       count(*)::text, count(*) = 0
FROM bookings b, p
WHERE b.event_id = p.eid
  AND NOT EXISTS (SELECT 1 FROM published_events e WHERE e.event_id = b.event_id)
    )

SELECT n, check_name, value, ok FROM checks
UNION ALL
SELECT 99, 'ALL CHECKS PASSED',
       count(*) FILTER (WHERE NOT ok)::text || ' failed',
    bool_and(ok)
FROM checks
ORDER BY n;