package com.t1.snezhko.task1.core.transaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateTransactionRequest {
    UUID producer;
    UUID consumer;
    BigDecimal amount;
}
