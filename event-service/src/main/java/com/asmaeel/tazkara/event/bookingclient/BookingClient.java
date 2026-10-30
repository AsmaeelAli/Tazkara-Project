package com.asmaeel.tazkara.event.bookingclient;

import com.asmaeel.tazkara.shared.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

@Component
public class BookingClient {

    private static final Logger log = LoggerFactory.getLogger(BookingClient.class);

    private final RestClient restClient;

    public BookingClient(@Value("${booking.base-url}") String baseUrl,
                         @Value("${api.service-key}") String serviceKey) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(6));
        factory.setReadTimeout(Duration.ofSeconds(6));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Service-Key", serviceKey)
                .requestFactory(factory)
                .build();
    }

    public void send(Object message) {
        try {
            restClient.post()
                    .uri("/internal/tickets")
                    .body(message)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            log.error("Could not connect to booking-service");
            throw new ServiceUnavailableException();
        } catch (RestClientResponseException e) {
            log.warn("booking-service returned HTTP {}: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ServiceUnavailableException();
        }
    }
}