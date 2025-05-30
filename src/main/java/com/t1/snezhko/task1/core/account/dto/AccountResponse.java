package com.t1.snezhko.task1.core.account.dto;

import com.t1.snezhko.task1.core.account.AccountType;
import com.t1.snezhko.task1.core.client.dto.ClientDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AccountResponse {
    Long id;
    ClientDTO client;
    AccountType accountType;
    BigDecimal amount;
}
