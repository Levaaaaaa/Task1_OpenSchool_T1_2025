package com.t1.snezhko.task1.core.transaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransactionRequest {
    Long producer;
    Long consumer;
    BigDecimal amount;
}
