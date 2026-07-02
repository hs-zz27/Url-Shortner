package com.hark.urlshortener.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Snowflake {
    private static final int MAX_SEQUENCE = (1 << 12) - 1;

    @Value("${app.machine-id}")
    private long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0;

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence++;
            if (sequence > MAX_SEQUENCE) {
                while (System.currentTimeMillis() == lastTimestamp) {
                    // busy wait
                }
                timestamp = System.currentTimeMillis();
                sequence = 0;
                lastTimestamp = timestamp;
            }
        } else {
            sequence = 0;
            lastTimestamp = timestamp;
        }
        return (timestamp << 22) | (machineId << 12) | sequence;
    }

}
