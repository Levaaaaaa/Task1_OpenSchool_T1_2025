package com.t1.snezhko.task1.core.account.dto;

import com.t1.snezhko.task1.core.account.AccountStatus;
import com.t1.snezhko.task1.core.account.AccountType;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
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
public class AccountResponse {
    UUID id;
    ClientDTO client;
    AccountType accountType;
    BigDecimal amount;
    BigDecimal frozenAmount;
    AccountStatus status;

}
