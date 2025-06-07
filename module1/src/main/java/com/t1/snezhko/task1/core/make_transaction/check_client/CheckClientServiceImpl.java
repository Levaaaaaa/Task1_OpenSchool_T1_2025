package com.t1.snezhko.task1.core.make_transaction.check_client;

import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientRequest;
import com.t1.snezhko.task1.core.make_transaction.dto.CheckClientResponse;
import com.t1.snezhko.task1.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.AuthenticationException;
import java.net.URI;
import java.util.Objects;

@Service
@Slf4j
class CheckClientServiceImpl implements CheckClientService{
    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.transactions.check-client-url}")
    private String url;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public CheckClientResponse checkClient(CheckClientRequest request) {
        String urlTemplate = UriComponentsBuilder
                .fromUri(URI.create(url))
                .queryParam("client-id", request.getClientId().toString())
                .queryParam("account-id", request.getAccountId().toString())
                .build()
                .toUriString();
        log.info("Checking request for client " + request.getClientId().toString() + " is ready to sending!");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateToken("t1-check-client-service"));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<CheckClientResponse> responseEntity = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, CheckClientResponse.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new IllegalStateException("Error with connecting to checking clients service! Response status - UNAUTHORIZED!");
        }
        log.info("Is client " + request.getClientId().toString() + " in black list - " + Objects.requireNonNull(responseEntity.getBody()).isBlackListed());
        return responseEntity.getBody();
    }
}
