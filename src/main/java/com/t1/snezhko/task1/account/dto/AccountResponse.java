package com.t1.snezhko.task1.account.dto;

import com.t1.snezhko.task1.account.AccountType;
import com.t1.snezhko.task1.client.dto.ClientDTO;
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
    Long id;
    ClientDTO client;
    AccountType accountType;
    BigDecimal amount;
}
