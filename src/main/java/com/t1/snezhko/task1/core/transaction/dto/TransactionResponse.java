package com.t1.snezhko.task1.core.transaction.dto;

import com.t1.snezhko.task1.core.account.dto.AccountResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransactionResponse {
    Long id;
    AccountResponse producer;
    AccountResponse consumer;
    BigDecimal amount;
    LocalDateTime transactionDate;
}
