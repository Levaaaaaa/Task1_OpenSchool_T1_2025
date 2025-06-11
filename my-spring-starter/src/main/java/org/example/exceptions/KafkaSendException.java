package com.t1.snezhko.task1.kafka;

public class KafkaSendException extends RuntimeException {
    public KafkaSendException(String message) {
        super(message);
    }
    public KafkaSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
