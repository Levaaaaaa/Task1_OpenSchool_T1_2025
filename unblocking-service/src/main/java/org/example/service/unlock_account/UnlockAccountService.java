package org.example.service.unlock_account;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UnlockAccountService {
    public void unlockAccount(UUID accountId);
}
