package com.t1.snezhko.task1.core.make_transaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CheckClientRequest {
    UUID clientId;
    UUID accountId;
}
