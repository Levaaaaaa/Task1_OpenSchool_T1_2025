package org.example.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "datasource_error_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DataSourceErrorLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long  id;

    @Column(name = "stack_trace", columnDefinition = "TEXT", nullable = false)
    String stackTrace;

    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "method_signature", nullable = false)
    String methodSignature;
}
