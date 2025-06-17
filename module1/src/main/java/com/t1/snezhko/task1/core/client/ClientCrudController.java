package com.t1.snezhko.task1.core.client;

import com.t1.snezhko.task1.core.client.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientCrudController {
    @Autowired
    private ClientService clientService;

    @PutMapping("/unlock/{id}")
    public ResponseEntity<Void> unlockClient(@PathVariable("id") String clientId) {
        clientService.updateClientStatus(UUID.fromString(clientId), ClientStatus.ACTIVE);
        return ResponseEntity.ok().build();
    }
}
