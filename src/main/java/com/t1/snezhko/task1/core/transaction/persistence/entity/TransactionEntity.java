package com.t1.snezhko.task1.core.transaction.persistence.entity;

import com.t1.snezhko.task1.core.account.persistence.entity.AccountEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "producer", referencedColumnName = "id", nullable = false)
    AccountEntity producer;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "consumer", referencedColumnName = "id", nullable = false)
    AccountEntity consumer;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    @Check(constraints = "amount > 0")
    BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    LocalDateTime transactionDate;
}
