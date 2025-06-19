package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.CheckClientRequest;
import org.example.dto.CheckClientResponse;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class CheckClientServiceImpl implements CheckClientService{
    @Override
    public CheckClientResponse checkClient(CheckClientRequest request) {
        //клиенты находятся в черном списке если последний символ их id имеет четный ASCII код
        String uuid = request.getClientId().toString();
        char lastUUIDSymbol = uuid.charAt(uuid.length() - 1);
        boolean clientBlackListed = lastUUIDSymbol % 2 == 0;
        log.info("Client " + request.getClientId().toString() + " has black list checking result - " + clientBlackListed);
        return buildResponse(request, clientBlackListed);
    }

    private CheckClientResponse buildResponse(CheckClientRequest request, boolean blackListed) {
        return CheckClientResponse.builder()
                .clientId(request.getClientId())
                .accountId(request.getAccountId())
                .blackListed(blackListed)
                .build();
    }
}
