package org.example.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public interface UnlockClientService {
    public void unlockClient(UUID clientId);
}
