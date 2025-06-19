package org.example.controllers;

import org.example.dto.CheckClientRequest;
import org.example.dto.CheckClientResponse;
import org.example.services.CheckClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CheckClientController {
    @Autowired
    private CheckClientService checkClientService;

    @GetMapping("/api/v1/clients/check")
    public ResponseEntity<CheckClientResponse> checkClient(@RequestParam("client-id")  String clientId, @RequestParam("account-id") String accountId) {
        CheckClientRequest request = CheckClientRequest.builder()
                .clientId(UUID.fromString(clientId))
                .accountId(UUID.fromString(accountId))
                .build();

        CheckClientResponse response = checkClientService.checkClient(request);
        return ResponseEntity.ok(response);
    }
}
