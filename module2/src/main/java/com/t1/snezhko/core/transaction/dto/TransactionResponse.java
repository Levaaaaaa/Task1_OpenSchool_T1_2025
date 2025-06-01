package com.t1.snezhko.core.transaction.dto;

import lombok.*;
import com.t1.snezhko.core.transaction.TransactionStatus;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransactionResponse {
    UUID transactionId;
    UUID producer;
    TransactionStatus status;

}
