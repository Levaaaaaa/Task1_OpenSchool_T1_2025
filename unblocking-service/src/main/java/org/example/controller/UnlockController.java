package org.example.controller;

import org.example.service.UnlockClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UnlockController {
    @Autowired
    private UnlockClientService unlockClientService;

    @PutMapping("/unlock/client/{id}")
    public ResponseEntity<Void> unlockClient(@PathVariable("id") String clientId) {
        unlockClientService.unlockClient(UUID.fromString(clientId));
        return ResponseEntity.ok().build();
    }
}
