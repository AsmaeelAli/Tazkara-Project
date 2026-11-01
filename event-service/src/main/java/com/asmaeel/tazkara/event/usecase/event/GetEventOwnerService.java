package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.repository.EventRepo;
import com.asmaeel.tazkara.shared.dto.OwnerResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetEventOwnerService {

    private final EventRepo eventRepo;

    public GetEventOwnerService(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    public OwnerResponse execute(UUID eventId) {

        UUID eventOwnerId = eventRepo.findById(eventId).get().getOwnerId();

        if (eventOwnerId == null) {
            return new OwnerResponse(null);
        }

        return new OwnerResponse(eventOwnerId);
    }
}
