package org.example.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.method-execution.time-limit")
@Data
@NoArgsConstructor
public class TimeLimitProperties {
    //    @Value("${app.method_execution.time_limit}")
    private long timeLimit;
}
