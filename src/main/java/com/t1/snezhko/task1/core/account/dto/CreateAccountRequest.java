package com.t1.snezhko.task1.core.account.dto;

import com.t1.snezhko.task1.core.account.AccountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccountRequest {
    UUID clientId;
    AccountType accountType;
    BigDecimal amount;
}
