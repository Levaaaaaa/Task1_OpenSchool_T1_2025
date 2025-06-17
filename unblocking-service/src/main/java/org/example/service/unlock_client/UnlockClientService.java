package org.example.service.unlock_client;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public interface UnlockClientService {
    public void unlockClient(UUID clientId);
}
