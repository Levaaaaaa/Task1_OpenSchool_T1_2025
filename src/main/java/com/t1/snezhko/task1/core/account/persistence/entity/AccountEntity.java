package com.t1.snezhko.task1.core.account.persistence.entity;

import com.t1.snezhko.task1.core.account.AccountType;
import com.t1.snezhko.task1.core.client.persistence.entity.ClientEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false)
    ClientEntity client;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    AccountType accountType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @Check(constraints = "amount > 0")
    BigDecimal amount;
}
