package com.t1.snezhko.task1.core.transaction.dto.check_status;

import com.t1.snezhko.task1.core.transaction.TransactionStatus;
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
public class CheckTransactionStatusResponse {
    UUID transactionId;
    UUID producer;
    TransactionStatus status;
    BigDecimal transactionAmount;
}
