# Tazkara – Event Ticketing Platform

Spring Boot microservices backend for an event-ticketing startup. Organizers create and publish events, attendees book
and pay for tickets, and the system never oversells and releases unpaid tickets automatically.

> Java Backend Development Internship – Final Assignment.
> **This README is a living document and is updated with every delivery.** See [Delivery Status](#2-delivery-status) and
> the [Changelog](#17-changelog).

---

## Table of Contents

1. [System Description](#1-system-description)
2. [Delivery Status](#2-delivery-status)
3. [Architecture](#3-architecture)
4. [How to Run](#4-how-to-run)
5. [Demo Accounts and Main Flow](#5-demo-accounts-and-main-flow)
6. [User Stories: Question, Solution, Tools Used](#6-user-stories-question-solution-tools-used)
7. [Design Decisions](#7-design-decisions)
8. [Security (JWT)](#8-security-jwt)
9. [Design Patterns Used](#9-design-patterns-used)
10. [API Conventions](#10-api-conventions)
11. [Docker Approach](#11-docker-approach)
12. [Git Workflow](#12-git-workflow)
13. [Assumptions](#13-assumptions)
14. [Known Limitations](#14-known-limitations)
15. [Not Implemented Stories: Plan](#15-not-implemented-stories-plan)
16. [Lightly Required Topics](#16-lightly-required-topics)
17. [Changelog](#17-changelog)

---

## 1. System Description

Tazkara (Arabic for "ticket") replaces chat apps and spreadsheets with one backend that solves three problems:

| Business problem                   | How the system solves it                                                                                                 |
|------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| Overselling                        | Every ticket is a database row. A ticket is taken with `SELECT ... FOR UPDATE SKIP LOCKED` inside one local transaction. |
| Unpaid reservations blocking seats | A configurable hold (default 10 minutes). A scheduled job expires overdue bookings and frees their tickets.              |
| No visibility on real sales        | Public availability numbers, "my bookings" views, and a CSV export of confirmed bookings for the organizer.              |

**Actors:** Visitor (no token), Attendee (books and pays), Organizer (manages events, cannot buy), System (background
jobs).

---

## 2. Delivery Status

| Story | Priority | Title                                | Status                                   |
|-------|----------|--------------------------------------|------------------------------------------|
| US-01 | MUST     | Register an account                  | Done                                     |
| US-02 | MUST     | Log in                               | Done                                     |
| US-03 | SHOULD   | View and update my profile           | Done                                     |
| US-04 | MUST     | Create an event                      | Done                                     |
| US-05 | SHOULD   | Edit a draft event                   | Done                                     |
| US-06 | MUST     | Publish an event                     | Done                                     |
| US-07 | MUST     | Browse and view events               | Done (availability fed by RabbitMQ)      |
| US-08 | SHOULD   | See my events                        | Done                                     |
| US-09 | SHOULD   | Cancel a published event             | **Not implemented** (plan in section 15) |
| US-10 | MUST     | Place a booking                      | Done                                     |
| US-11 | MUST     | Pay for a booking                    | Done                                     |
| US-12 | MUST     | Release unpaid tickets automatically | Done                                     |
| US-13 | MUST     | See my bookings                      | Done                                     |
| US-14 | SHOULD   | Cancel a booking                     | **Not implemented** (plan in section 15) |
| US-15 | SHOULD   | Follow sales of my event             | **Not implemented** (plan in section 15) |
| US-16 | SHOULD   | Export confirmed bookings as CSV     | Done                                     |
| US-17 | SHOULD   | Be notified about booking changes    | Done                                     |

All **MUST** stories are implemented. The three unfinished SHOULD stories (US-09, US-14, US-15) are documented with the
exact approach that will be used, so the design is complete even where code is not.

---

## 3. Architecture

```
                 ┌───────────────────── ONE Docker container ─────────────────────┐
                 │                                                                 │
 Client          │  ┌─────────────┐   ┌──────────────┐   ┌───────────────┐         │
 (Swagger,  ───► │  │ Identity    │   │ Event        │   │ Booking       │         │
  Postman)       │  │ 8081        │   │ 8082         │   │ 8083          │         │
  via Gateway    │  │ JWT issuer  │   │ events,      │   │ tickets,      │         │
  (8080)         │  │ users       │   │ ticket types │   │ bookings,     │         │
                 │  └──────┬──────┘   └───┬─────▲────┘   │ payment,      │         │
                 │         │              │     │        │ expiry, CSV   │         │
                 │    identity DB     event DB  │        └───┬────▲─────┘          │
                 │                              │            │    │                │
                 │    Publish call (sync REST) ─┘────────────►    │                │
                 │    Owner check (sync REST) ◄──────────────┘    │                │
                 │    AvailabilityChanged (RabbitMQ) ◄───────────┘                 │
                 │                                                                 │
                 │                 PostgreSQL          RabbitMQ                    │
                 └─────────────────────────────────────────────────────────────────┘
```

**Rules**

- Each service owns its data and never reads another service's database.
- Each service has its own `pom.xml`, configuration, `@RestControllerAdvice` and OpenAPI docs.
- Each service verifies the JWT **locally**. Only Identity issues tokens.
- The API gateway only routes requests. It does not verify tokens (each service does), and it has no route to
  `/internal/**`, so internal endpoints cannot be reached from outside.

**Maven modules:** `tazkara-parent` (root), `tazkara-shared` (security filter, JWT verifier, error format, shared
exceptions, message contracts), `identity-service`, `event-service`, `booking-service`, `api-gateway-service`.

**Stack:** Java 17, Spring Boot 3.5.x, Spring Security, Spring Data JPA / Hibernate, PostgreSQL, RabbitMQ, jjwt 0.12.6,
springdoc OpenAPI, Spring Cloud Gateway, Docker (multi-stage), Maven wrapper.

---

## 4. How to Run

Requirement: Docker only.

```bash
git clone <repo-url>
cd <repo>
cp .env.example .env      # safe development values, use as is
docker compose up --build
```

| Service  | Port | Swagger UI                                          |
|----------|------|-----------------------------------------------------|
| Gateway  | 8080 | `http://localhost:8080/swagger-ui.html` (all three) |
| Identity | 8081 | `http://localhost:8081/swagger-ui.html`             |
| Event    | 8082 | `http://localhost:8082/swagger-ui.html`             |
| Booking  | 8083 | `http://localhost:8083/swagger-ui.html`             |

Configuration comes from environment variables defined in `.env` (copy of `.env.example`): JWT secret, internal service
key, database and RabbitMQ credentials, hold duration, expiry job frequency, token lifetime. The values in
`.env.example` are **marked development defaults**. PostgreSQL data survives restarts through a named volume.

Build without Docker: `./mvnw clean package` from the repository root.

---

## 5. Demo Accounts and Main Flow

Demo data on first start: 1 organizer, 1 attendee, 1 published event with 2 ticket types.

| Role      | Email                 | Password     |
|-----------|-----------------------|--------------|
| ORGANIZER | `asmaeel@gmail.com` | `Password2001` |
| ATTENDEE  | `User@tazkara.com`    | `Password2001` |

**Main flow (Swagger UI, use "Authorize" with the token):**

1. Log in as attendee (Identity) and copy the token.
2. Browse public events (Event, no token) and see ticket types with available counts.
3. Place a booking (Booking): returns `PENDING_PAYMENT`, a reference such as `TZK-…` and `holdExpiresAt`.
4. Pay with `{"paymentMethod":"CARD"}`: status becomes `CONFIRMED`. Paying again is rejected. A notification line
   appears in the Booking log.
5. Wait for the hold to pass without paying: the job sets the booking to `EXPIRED` and frees the tickets.
6. Open "my bookings" and a booking by reference.
7. Log in as the organizer and download `GET /api/organizer/events/{eventId}/export` to get the CSV of confirmed
   bookings.

---

## 6. User Stories: Question, Solution, Tools Used

Each story follows the same layout: **the question** (what the requirement asks), **the solution** (how it was solved),
**tools used**.

### Identity Service

#### US-01 Register an account (MUST) – Done

**Question:** A visitor creates an account as attendee or organizer. Email unique ignoring case, password 8+ with letter
and digit, never stored in plain text, no admin role. **Solution:**

- Request validated with Bean Validation (name, email, password 8–72 with letter and digit, role enum `ATTENDEE`/
  `ORGANIZER`).
- Email is trimmed and lowercased in one place and stored normalized; duplicates return **409** `ConflictException`.
- Organizer must provide an organization name (3–30 letters/digits/spaces); attendee must not. Stored in a separate
  organization table with a unique normalized key.
- Password hashed with BCrypt; responses never contain it.

**Tools:** Spring Security (BCrypt), Bean Validation, Spring Data JPA, `@RestControllerAdvice`.

#### US-02 Log in (MUST) – Done

**Question:** Login with email and password, receive a signed JWT and its expiry (30 min, configurable). Wrong email and
wrong password give the same error. **Solution:** `LoginUserService` verifies BCrypt; on failure always throws the same
`BadCredentialsException` (401, same message). `JwtService` signs an HS256 token with claims `sub` (user id), `email`,
`role`, `iat`, `exp`. Secret from the `JWT_SECRET` environment variable; lifetime from configuration. **Tools:** jjwt
0.12.6, Spring Security, environment-based configuration.

#### US-03 View and update my profile (SHOULD) – Done

**Question:** Show id, name, email, role, organization name; allow changing only name and organization name.
**Solution:** `GET/PUT` profile endpoints resolve the user from the token (never from a request id). The update DTO
contains only name and organization name, so email and role cannot be changed by design. **Tools:** Spring Security
principal (`AuthenticatedUser`), DTOs, Bean Validation.

### Event Service

#### US-04 Create an event (MUST) – Done

**Question:** Organizer creates an event with 1–5 ticket types; start at least 24 h ahead; new event is DRAFT and owned
by creator. **Solution:** `ORGANIZER`-only endpoint; validation for ticket types (unique name per event, price ≥ 0,
quantity 1–10,000) and start time; status starts as `DRAFT`; owner id comes from the token. **Tools:** Spring Security
`hasRole`, Bean Validation, JPA, `BigDecimal` for price, `Instant` (UTC).

#### US-05 Edit a draft event (SHOULD) – Done

**Question:** Owner changes a DRAFT event with the same validations; PUBLISHED/CANCELLED cannot be edited. **Solution:**
Ownership check; the event entity itself refuses edits unless `DRAFT` (business error 422). **Tools:** Rich domain
model, `BusinessRuleException`.

#### US-06 Publish an event (MUST) – Done

**Question:** Owner publishes a DRAFT whose start is still 24 h+ away; afterwards it is public and bookable.
**Solution:** `publish()` lives in the event entity (DRAFT only, start check). On success Event calls Booking
**synchronously** through a REST client (`RestClient`), sending the event data and its ticket types. The call carries
the shared internal key (from an environment variable) in a header, so only the services can use it. Booking then
creates its own copy of the event and the individual ticket rows. **Tools:** Domain method, Spring `RestClient`, shared
internal key (environment variable).

#### US-07 Browse and view events (MUST) – Done

**Question:** Public (no token): only PUBLISHED, not started, soonest first, paginated; filters city, date range, title
keyword; each ticket type with price and tickets still available (sold out marked); details endpoint; others are 404.
**Solution:**

- Public endpoints under `/api/public/events` with a combinable-filter query and pagination, plus a details endpoint.
- **Availability** is read from Event's own small table `updated_event_data` (available, held, sold per ticket type),
  updated **only** by `AvailabilityChanged` messages from Booking. Event never calls Booking and never uses these
  numbers for decisions.
- The update is an upsert per ticket type, and a message older than the stored `last_updated` is ignored (idempotent and
  order-safe).

**Tools:** Spring Data JPA (filters + `Pageable`), RabbitMQ `@RabbitListener`, Jackson JSON message converter.

#### US-08 See my events (SHOULD) – Done

**Question:** Organizer lists own events in any status, paginated, filter by status. **Solution:** Query by owner id
from the token plus optional status filter; always paginated. **Tools:** Spring Data JPA, `Pageable`.

#### US-09 Cancel a published event (SHOULD) – **Not implemented**

See [section 15](#15-not-implemented-stories-plan).

### Booking Service

#### US-10 Place a booking (MUST) – Done ! **[ THE HARDEST ONE ]**

**Question:** An attendee books tickets of one event. The event must be published and not started, organizers cannot
book, at most 6 tickets per event across active bookings, all or nothing, never oversell (two people taking the last
ticket: exactly one succeeds), prices come from the server, unique hard-to-guess reference, 10-minute hold, free
bookings confirmed immediately.

**The idea:** Overselling is a concurrency problem, so the design makes the database decide who wins, instead of
counting numbers in Java.

1. **Tickets are real rows, created in advance.** When an event is published, Booking receives the event and its ticket
   types and creates **one row per ticket** (`FREE`) using **batch inserts**. This is done once, at publish time, so the
   booking request itself only has to *claim* rows and never has to create them. In a separate experiment,
   batch-inserting 1,000,000 ticket rows took about 15–17 seconds, so an event of a few hundred tickets is almost
   instant. *(50000 row AVG = 510ms)*
2. **Booking = claiming rows.** A booking moves the requested number of `FREE` tickets to `HELD` and links them to the
   booking. Payment moves them to `SOLD`; expiry or cancellation moves them back to `FREE`. The valid moves are enforced
   by the ticket status enum (`FREE → HELD → SOLD`, `HELD → FREE`, `SOLD → FREE`).
3. **The database picks the winner.** Tickets are selected with `SELECT ... FOR UPDATE SKIP LOCKED`. If two requests
   race for the last ticket, one locks the row; the other **skips** the locked row, finds nothing free, and fails.
   Exactly one succeeds, nobody waits, and there is no deadlock queue.

**Solution (one local transaction):**

1. Only `ATTENDEE` can book (organizers get 403). The event copy in Booking must be `PUBLISHED` and not started.
2. **Limit of 6 per event:** a per- (attendee, event) lock serializes parallel requests of the same attendee, then the
   limit is checked on active bookings (`PENDING_PAYMENT` + `CONFIRMED`; `EXPIRED` and `CANCELLED` do not count).
   Without this lock, two simultaneous requests could both pass the check.
3. **All or nothing:** for each requested ticket type the needed `FREE` tickets are claimed with `SKIP LOCKED` and set
   to `HELD`. If any type does not have enough, the whole transaction **rolls back** with `TICKETS_UNAVAILABLE`, so no
   partial booking can exist.
4. **Server-side prices:** the client sends only event id, ticket type ids and quantities. Prices, title and start time
   come from Booking's own copy of the event; the total is computed on the server with `BigDecimal`, and the unit price
   is saved in the booking item, so later price changes never affect it.
5. **Reference:** `TZK-` + 12 random uppercase letters/digits from `SecureRandom`, with a unique database constraint and
   a retry on the rare collision. It is separate from the internal UUID id, which is never exposed.
6. **Hold:** `holdExpiresAt = now + hold duration` (default 10 minutes, configurable). A booking with total zero is
   `CONFIRMED` immediately and its tickets become `SOLD`.
7. **After commit** (never before), an application event triggers the availability message to Event (see US-07). If the
   transaction rolls back, nothing is sent.

**Why this design (alternatives rejected):**

- *A counter column per ticket type* (`available = available - n`): every booking would lock the same row (a hot row),
  which serializes all buyers of an event and removes the benefit of row-level concurrency.
- *Event owning the stock and Booking reserving through a synchronous call:* a crash between the two services would leak
  stock and would need compensation logic. Keeping tickets and bookings in the same database makes the whole operation
  one ACID transaction.
- *Plain `FOR UPDATE` (without `SKIP LOCKED`):* the second buyer would wait for the first and then fail; `SKIP LOCKED`
  fails fast and keeps throughput high.

**Load test (Apache JMeter):** the flow was tested with concurrent users who register, log in and then book tickets for
the **same event** (each user picks a different ticket type, some of them also pay). The goal was to verify that:

- tickets sold + held never exceed the event capacity (no overselling);
- no ticket row belongs to two bookings;
- no attendee holds more than 6 tickets for the event;
- every response is either a successful booking or a clean business error (`TICKETS_UNAVAILABLE` / limit exceeded),
  never a server error, and no response exposes another user's data.

*TODO(Asmaeel): write the real outcome here (number of users, capacity, how many bookings succeeded, how many were
rejected with `TICKETS_UNAVAILABLE`, and the result of the consistency checks above).*

**Tools:** JPA pessimistic locking with `SKIP LOCKED`, `@Transactional`, Hibernate JDBC batch inserts, `SecureRandom`,
`BigDecimal`, Spring `ApplicationEventPublisher` + `@TransactionalEventListener(AFTER_COMMIT)`, RabbitMQ, Apache JMeter.

**NOTE:** Browse the booking service and you will see `photos` and `test.SQL` file.

#### US-11 Pay for a booking (MUST) – Done

**Question:** Simulated payment (CARD or WALLET), owner only; rejected if not pending, hold passed (even if the job has
not run), or event cancelled/started; paying twice never confirms twice. **Solution:**
`POST /api/bookings/{reference}/payment`. The booking is found by reference **and** attendee id from the token
(otherwise 404). The domain method `confirm(now)` accepts only `PENDING_PAYMENT` before `holdExpiresAt`; tickets move
`HELD → SOLD` in the same transaction. A second payment fails because the status is already `CONFIRMED`. **Tools:** Rich
domain model, `@Transactional`, optimistic locking (`@Version`).

#### US-12 Release unpaid tickets automatically (MUST) – Done

**Question:** A background process expires overdue `PENDING_PAYMENT` bookings and frees tickets; configurable hold and
frequency; if the job and a payment hit the same booking, exactly one wins. **Solution:** A scheduled job runs on
Spring's scheduler thread at a configurable interval (default every 30 seconds, set in Docker). For each overdue booking
it calls `expire(now)` and releases tickets (`HELD → FREE`) in one transaction. Conflicts with payment are resolved by
`@Version` optimistic locking: the first commit wins; the loser re-reads and fails cleanly (payment → 422, job → skips).
**Tools:** `@Scheduled`, `@Transactional`, JPA `@Version`, environment configuration.

#### US-13 See my bookings (MUST) – Done

**Question:** Attendee lists own bookings (newest first, paginated, filter by status) and opens one by reference with
full details; another user's booking is never visible (404). **Solution:** Dynamic filter through JPA Specifications on
(attendee id from token, optional status); sorted by creation time descending. Detail lookup by reference **and**
attendee id, so someone else's booking is "not found". Response is a DTO without internal ids. **Tools:** Spring Data
JPA Specifications, `Pageable`, DTO mapper.

#### US-14 & US-15 – **Not implemented**

See [section 15](#15-not-implemented-stories-plan).

#### US-16 Export confirmed bookings as CSV (SHOULD) – Done

- `GET /api/organizer/events/{eventId}/export` returns a CSV file download with only `CONFIRMED` bookings, one row per
  ticket type (reference, attendee email, ticket type, quantity, unit price, line total, confirmed at).
- The file is generated with Java I/O classes (`BufferedWriter` over `OutputStreamWriter`, UTF-8,
  `ByteArrayOutputStream`). Commas, quotes and line breaks are escaped correctly. Bookings are read page by page from
  the database.
- The file name contains the event id and the export date (UTC).
- **Ownership check (new internal REST call):** the Booking Service calls a new internal endpoint in the Event Service,
  `GET /internal/events/{eventId}/owner`, to verify that the caller owns the event before exporting. The call is
  synchronous because the answer decides whether the export may continue. The endpoint is not exposed through the
  gateway and is protected by a shared service key header (`X-Service-Key`, taken from an environment variable). A
  non-owner gets 403, an unknown event gets 404.

#### US-17 Booking notifications (SHOULD) – Done

- **Observer pattern:** on `CONFIRMED`, `CANCELLED` and `EXPIRED`, the booking logic publishes a Spring application
  event (`BookingStatusChangedEvent`) and knows nothing about who listens. A separate listener handles it, so adding a
  second listener (for example an audit log) needs no change in the booking code. The listener already supports
  `CANCELLED`; that notification is triggered once US-09 and US-14 are implemented.
- The listener is a transactional event listener (`AFTER_COMMIT`): a notification is sent only if the booking
  transaction really committed, so a rolled-back payment never produces a false notification.
- **Factory pattern:** `NotificationChannelFactory` returns the right channel (`EMAIL`, `PUSH`, `SMS`) through a
  `switch` on the channel type. Each channel is its own class with one job.
- All notifications are simulated by log messages (recipient email, subject, message). No real message is sent.
- Availability changes are sent from Booking to Event through RabbitMQ, also published after the transaction commits
  (transactional listener), so Event never receives a count for a transaction that was rolled back.

---

## 7. Design Decisions

### 7.1 Synchronous vs asynchronous communication

| Interaction                                                  | Type                                  | Why                                                                                                                                                                                                                         |
|--------------------------------------------------------------|---------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Event → Booking: publish notification (event + ticket types) | **Sync** (REST client + internal key) | Booking must confirm it created its copy and ticket rows before the event counts as published; otherwise a published event could exist that cannot be booked.                                                               |
| Booking → Event: `AvailabilityChanged`                       | **Async** (RabbitMQ)                  | Display-only counters; a delay of seconds is acceptable; keeps booking fast and independent. This flow was built and tested end to end with RabbitMQ (Booking producer → exchange → Event queue → listener → counts table). |
| Booking → Event: ownership check (US-16, planned for US-15)  | **Sync** (REST + internal key)        | The answer decides the next step and the user is waiting.                                                                                                                                                                   |
| Booking: notifications (US-17)                               | **In-process** (Spring events)        | Same service, same transaction outcome: a listener reacts after commit. No network is needed.                                                                                                                               |

#### Messaging rule and RabbitMQ

Rule used: async when nobody waits for the answer and retries are safe, sync when the answer decides what happens next.

**How RabbitMQ works here (short):**

- A service never sends a message directly to another service. The producer publishes the message to an **exchange**,
  and RabbitMQ routes it to the right **queue** using a **routing key**. The consumer service reads from its queue.
- We use one shared **topic exchange**, `tazkara.events`. Each message type has its own routing key (today:
  availability changed; event cancelled is planned for US-09).
- **The consumer owns its queue.** For example, Event owns the availability queue and the listener that updates its
  counters. Queue names come from each service's configuration. The exchange name and routing keys are constants.
- Messages are JSON.
- If the consumer service is down, the messages wait in the durable queue and are processed when it returns, so nothing
  is lost.
- Messages carry ids and absolute values, so processing the same message twice does not corrupt data.

#### Configuration and environment variables

- All environment variables are defined in a single `.env` file, and the repository includes a `.env.example` copy with
  safe development values. To run the project, copy `.env.example` to `.env` (or use it as is), then run
  `docker compose up --build`.
- `.env` is listed in `.gitignore`, so real secrets are never committed.
- Each service reads only the properties it needs and owns its own configuration, including its own queue names.
- Secrets and durations come from configuration, never from hard-coded values: the JWT secret, the internal service key
  (`X-Service-Key`), database credentials, token expiry, hold duration and expiry job frequency.
- Every variable was checked against the running system: each one is read by the service that needs it, and the project
  starts from a fresh clone with the documented command.

### 7.2 Who owns the tickets? (never oversell)

**Booking owns the inventory:** one row per ticket (`FREE`/`HELD`/`SOLD`). Event keeps only display counters, never used
for decisions. Reasons:

- Hold, pay, expire and cancel run in **one local transaction**; no distributed transaction.
- If Event is down, booking still works (Booking has its own copy of published events and ticket types).
- Published events are immutable (edit only in DRAFT), so the copy cannot go stale except by cancellation.

**Rejected alternative:** Event owns stock and Booking reserves through a synchronous call. A crash between the two
services would leak stock and would need compensation logic.

### 7.3 Overselling prevention

See US-10: row-level locks with `SKIP LOCKED` in one transaction. Two buyers of the last ticket: one locks the row, the
other skips it, finds nothing, and fails with `TICKETS_UNAVAILABLE`. A per- (attendee, event) lock prevents two parallel
requests from passing the 6-ticket limit together.

### 7.4 Availability numbers

After each commit that changes tickets, Booking reads all counts of that event with **one grouped query**
(`group by ticket type, status`), builds one message with **absolute** numbers, and publishes it. Counting from the
ticket table (not a separate counter table) keeps one source of truth and avoids a hot row that every booking would have
to lock.

### 7.5 Expiry job and multi-threading

See US-12. The job runs on the scheduler thread; correctness comes from the database transaction and optimistic locking,
not Java `synchronized` (which would not work across instances).

### 7.6 Partial failures

| Failure                                         | Behaviour                                                                                                 |
|-------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| Event down                                      | Booking continues (local copy). Messages to Event wait in the durable queue.                              |
| Booking down while an event is published        | *TODO(Asmaeel): state what your code does (publish rejected with 503? event stays DRAFT? retried?).*      |
| Booking down during CSV export                  | Not applicable (export runs inside Booking). If Event is down, the ownership check fails with 503.        |
| Booking crashes after commit, before publishing | Event counters stay stale until the next message. No overselling, because decisions use Booking's tables. |
| Duplicate or out-of-order messages              | Handlers are idempotent; absolute values; older messages are ignored by timestamp.                        |
| Publishing to RabbitMQ fails                    | Logged as a warning; the booking is **not** rolled back (it already succeeded).                           |

### 7.7 Service-to-service security

Synchronous service-to-service calls use a **shared internal key** (environment variable) sent in the `X-Service-Key`
request header and accepted only on a dedicated `/internal/**` path. It is used by the publish call (Event → Booking,
`RestClient`) and by the ownership check (Booking → Event, used by the CSV export and planned for US-15). The gateway
has no route to `/internal/**`, and it removes the header from incoming requests, so it is never open to the public.

---

## 8. Security (JWT)

| Topic        | Implementation                                                                                                                |
|--------------|-------------------------------------------------------------------------------------------------------------------------------|
| Issuing      | Only Identity (`JwtService`).                                                                                                 |
| Algorithm    | HS256, secret ≥ 32 chars from `JWT_SECRET` (marked dev default in `.env.example`).                                            |
| Claims       | `sub`, `email`, `role`, `iat`, `exp`. Nothing sensitive.                                                                      |
| Verification | Each service verifies signature and expiry locally (`JwtVerifier` + `JwtAuthFilter` in `tazkara-shared`), no database lookup. |
| 401 vs 403   | Missing/invalid/expired token → 401; valid token without permission → 403; same JSON error format.                            |
| Roles        | Only organizers manage events; only attendees book and pay; no admin role exists.                                             |
| Ownership    | Own events and bookings only; another user's booking returns 404.                                                             |
| Passwords    | BCrypt; never logged or returned.                                                                                             |
| Login errors | Same generic error for wrong email and wrong password.                                                                        |
| Sessions     | Stateless.                                                                                                                    |

**Why CSRF is disabled:** CSRF abuses credentials the browser attaches automatically (cookies/sessions). This API is
stateless, uses no cookies, and the token is sent explicitly in the `Authorization` header, which a third-party site
cannot add. There is nothing for CSRF to exploit.

---

## 9. Design Patterns Used

| Pattern                | Where                                                                                                                                                                                                                              | Why                                                                                                                                                                                                                                                  |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Observer / Pub-Sub** | Booking publishes a Spring application event (`BookingStatusChangedEvent`) after commit; separate listeners react (notifications, availability publishing). RabbitMQ pub-sub between services.                                     | The booking code does not know who listens; adding a listener needs no change in it.                                                                                                                                                                 |
| **MVC**                | Thin controllers → use-case services → repositories → entities; DTOs as the response contract.                                                                                                                                     | Separation of concerns.                                                                                                                                                                                                                              |
| **Singleton**          | Spring-managed beans (default scope), e.g. `JwtService`, `JwtVerifier`.                                                                                                                                                            | One shared stateless instance managed by the container.                                                                                                                                                                                              |
| **Factory**            | `NotificationChannelFactory` returns the right channel (`EmailChannel`, `PushChannel` or `SmsChannel`, all implementing `NotificationChannel`) from a `NotificationType` using a `switch`. It is called by `BookingListener` (Booking Service). | The listener only knows the `NotificationChannel` interface, so it has no `new` and no `if/else` on the channel type. Each channel is its own class with one job. Adding a new channel means adding one class and one `switch` case; the listener does not change. |
| **Strategy (bonus)**   | Not implemented (planned for refund policies, US-14).                                                                                                                                                                              |                                                                                                                                                                                                                                                      |

Also used: **use-case classes**, a **rich domain model** (entities enforce their own status transitions).

**Why use-case classes with an `execute` method?**

- Each business action (create event, place booking, pay booking, export CSV...) is its own class with one public
  method, `execute`. The name of the class says what the action does, and every use-case has the same shape, so the code
  is easy to find, read and explain.
- This follows the **Single Responsibility Principle**: one class has one job and one reason to change. Controllers
  only receive the request and return the response. Use-case classes run the business action. Repositories only talk to
  the database. Entities protect their own rules (for example, an `EXPIRED` booking can never become `CONFIRMED`).
- Small classes are easy to read, change and test without touching other parts of the system, and a mistake stays in
  one place.

**The idea behind it:**

- This is inspired by the story of Richard Feynman (Physicist) at Los Alamos. He split a big calculation into very
  simple steps, and each person (or machine) only knew one simple operation, such as adding or multiplying. The big
  result came from putting the small results together. We tried to build the system the same way: every class does one
  small thing well, and the big behavior (a full booking, payment or cancellation) comes from combining them.

---

## 10. API Conventions

- RESTful resources and correct status codes; DTOs only (JPA entities are never returned); Bean Validation.
- One `@RestControllerAdvice` per service; consistent JSON error:
  `timestamp, status, error code, message, path, field errors`.
- **409** conflicts, **422** business-rule violations (e.g. `TICKETS_UNAVAILABLE`), **400** invalid input, **401/403**
  security, **404** not found, **500** generic message without stack trace. `DataIntegrityViolationException` returns a
  fixed message.
- **Currency:** one currency for the whole platform, **USD**. Prices carry no currency field because there is only one.
  Money is `BigDecimal` (scale 2). Time is `java.time` in UTC (`Instant`).
- Every list is paginated: **default page size 10, maximum 50**. A page below 0 is treated as 0, and a size above the
  maximum is reduced to 50.
- Responses omit null fields; internal database ids and versions of bookings are never exposed (a booking is identified
  by its reference).
- SLF4J logging only; no passwords or tokens in logs.

---

## 11. Docker Approach

- **Multi-stage Dockerfile:** the first stage uses Maven to compile and package all modules inside Docker, so no local
  Java or Maven is needed. The final stage is a small Java runtime image that only holds the built jars.
- **One container runs several processes:** Docker normally runs one main process per container, so a process manager
  is needed to start and watch all the services together. *TODO(Asmaeel): state the exact tool you used (supervisord
  or a start script) and one sentence on how it works.*
- **Services wait for the database:** *TODO(Asmaeel): state how (healthcheck + `depends_on`, or a retry loop).*
- **Data survives restarts:** a named volume holds the PostgreSQL data.
- Only the needed ports are exposed, and all configuration comes from environment variables (`.env`).
- Hibernate schema generation is used for convenience; **it is not a production practice** (production would use
  Flyway/Liquibase).

---

## 12. Git Workflow

- Repository created on day 1 with a proper `.gitignore`.
- Small commits, one logical change each, imperative messages (`feat(booking): ...`), on feature branches merged into
  `main` (no squash, no rewritten history).
- `main` always builds (`./mvnw clean verify` before merging).

---

## 13. Assumptions

- Only `ATTENDEE` and `ORGANIZER` roles. Organizer must give an organization name; attendee must not.
- Extra rules: max 10 events per organizer; event title unique per owner.
- Ticket limit (6) is per attendee **per event**, across all ticket types and active bookings. A booking is always for
  one event.
- Hold starts at booking creation; default 10 minutes, configurable.
- Expired bookings stay visible as `EXPIRED` and are never payable.
- Free bookings are confirmed immediately.
- Another user's booking returns 404 (not 403).
- Event availability numbers can lag by seconds; a user may see "available" and still receive `TICKETS_UNAVAILABLE`.
  This is by design.
- One currency (USD), no conversion.

---

## 14. Known Limitations

- **Three SHOULD stories are not implemented** (US-09, US-14, US-15) because of time. See
  [section 15](#15-not-implemented-stories-plan).
- No transactional outbox: a crash between commit and message publish leaves Event counters stale until the next message
  (display only).
- Event counters are eventually consistent by design.
- Hibernate `ddl-auto` instead of migrations.
- Shared symmetric JWT secret (HS256): every verifying service could also sign. RS256 would be safer.
- No refresh tokens or token revocation.
- No rate limiting (e.g. booking attempts per user).
- Ticket rows of finished events are not purged; a scheduled cleanup after a retention period is future work (storage is
  small: about 15,000 rows per day at 50 events of 300 tickets).
- Payments and notifications are simulated (notifications are log messages only).
- The CSV export is built in memory as a byte array (read page by page from the database); a very large event would be
  better served by streaming the file directly to the response.
- **Automated tests:** no automated test suite. The system was tested manually through Swagger UI and Postman, and with
  a JMeter load test (many attendees booking different ticket types at the same time, some of them paying) to check that
  tickets are never oversold.

---

## 15. Not Implemented Stories: Plan

These stories were not finished in time. For each, this is the **design that would be used**, based on what already
exists.

### US-09 Cancel a published event

- **Event:** owner only, `PUBLISHED` only; the event entity moves to `CANCELLED` and notifies Booking through a durable
  RabbitMQ message (`EventCancelled`), so it survives Booking being down.
- **Booking:** a listener marks the event copy as cancelled and, for every `PENDING_PAYMENT` and `CONFIRMED` booking of
  that event, calls the existing domain method `cancel(EVENT_CANCELLED, refund)` (full refund for confirmed ones),
  releases the tickets, then triggers the normal availability message and the existing notification event.
- **If Booking is down:** the message stays in the durable queue and is processed when Booking returns; the handler is
  idempotent (already-cancelled bookings are skipped).

### US-14 Cancel a booking

- Owner only (found by reference + attendee id from the token, otherwise 404). `EXPIRED`/`CANCELLED` cannot be cancelled
  again.
- `PENDING_PAYMENT`: cancel with no refund. `CONFIRMED`: refund by time left before the start (7+ days 100%, 48 h to 7
  days 50%, under 48 h not allowed).
- The refund calculation would be a **Strategy pattern** (one class per policy) passed into the existing
  `cancel(reason, refund)`; tickets return to `FREE`; availability and notification events are published after commit.

### US-15 Follow sales of my event

- Booking verifies ownership through the **synchronous** internal endpoint in Event that already exists for the CSV
  export (see 7.7).
- Paginated bookings with a status filter (reference, attendee email, tickets, status) and a per-ticket-type summary:
  total, sold, held, remaining from the ticket table; revenue from `CONFIRMED` booking items (saved unit price ×
  quantity).

---

## 16. Lightly Required Topics

| Topic                | Where                                                                                                                                                               |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Multi-threading      | US-12: scheduled expiry job on the scheduler thread, with optimistic locking against concurrent payment.                                                            |
| Maven (multi-module) | Parent POM with modules `tazkara-shared`, `identity-service`, `event-service`, `booking-service`, `api-gateway-service`; builds with `mvn clean package`.           |
| Files and I/O        | US-16: CSV export generated with `BufferedWriter`, `OutputStreamWriter` and `ByteArrayOutputStream` (UTF-8), with correct escaping, returned as a file download.     |

---

## 17. Changelog

| Date (UTC) | Change                                                                                                                                                                                                                                                                                                                                |
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 2026-11-01 | Corrected communication map: publish notification is synchronous REST with internal key; availability updates are RabbitMQ. README restructured per user story. Booking → Event availability messaging (US-07) added. Plans for US-09, US-14 to US-17 documented.                                                                    |
| 2026-11-01 | US-16 (CSV export with owner check through a new internal Event endpoint) and US-17 (Observer + Factory notifications, log only) implemented. API gateway documented. Environment variables moved to `.env` / `.env.example`. RabbitMQ explanation, Docker, API conventions and limitations sections completed. Only US-09, US-14, US-15 remain. |