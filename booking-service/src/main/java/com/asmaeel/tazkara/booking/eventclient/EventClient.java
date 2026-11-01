package com.asmaeel.tazkara.booking.eventclient;

import com.asmaeel.tazkara.shared.dto.OwnerResponse;
import com.asmaeel.tazkara.shared.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class EventClient {

    private static final Logger log = LoggerFactory.getLogger(EventClient.class);

    private final RestClient restClient;

    public EventClient(@Value("${event.base-url}") String baseUrl,
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

    public UUID getOwnerId(UUID eventId) {
        try {
            return restClient.get()
                    .uri("/internal/events/{id}/owner", eventId)
                    .retrieve()
                    .body(OwnerResponse.class)
                    .ownerId();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("Event not found");
        } catch (ResourceAccessException e) {
            log.error("Could not connect to event-service");
            throw new ServiceUnavailableException();
        } catch (RestClientResponseException e) {
            log.warn("event-service returned HTTP {}", e.getStatusCode());
            throw new ServiceUnavailableException();
        }
    }
}
