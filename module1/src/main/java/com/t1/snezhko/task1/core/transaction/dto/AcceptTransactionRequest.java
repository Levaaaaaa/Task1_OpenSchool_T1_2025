package com.t1.snezhko.task1.core.transaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptTransactionRequest {
    UUID clientId;
    UUID accountId;
    UUID transactionId;
    LocalDateTime timestamp;
    BigDecimal transactionAmount;
    BigDecimal accountBalance;


}
