package com.t1.snezhko.task1.core.make_transaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckClientResponse {
    UUID clientId;
    UUID accountId;
    boolean blackListed;
}
