package com.asmaeel.tazkara.event.usecase.event;

import com.asmaeel.tazkara.event.repository.EventRepo;
import org.springframework.stereotype.Service;

@Service
public class UpdateDraftEventService {

    private final EventRepo eventRepo;
    private final CheckEventOwnerService checkEventOwnerService;

    public UpdateDraftEventService(EventRepo eventRepo, CheckEventOwnerService checkEventOwnerService) {
        this.eventRepo = eventRepo;
        this.checkEventOwnerService = checkEventOwnerService;
    }


}
