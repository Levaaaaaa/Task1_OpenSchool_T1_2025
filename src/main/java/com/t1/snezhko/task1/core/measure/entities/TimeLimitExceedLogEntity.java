package com.t1.snezhko.task1.core.measure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_limit_exceed_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeLimitExceedLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long time;

    @Column(name = "method_signature", nullable = false, length = 255)
    private String methodSignature;

    @Column(name = "method_start_time", nullable = false)
    LocalDateTime methodStartTime;
}
