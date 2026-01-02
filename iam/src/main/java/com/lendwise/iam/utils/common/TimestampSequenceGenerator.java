package com.lendwise.iam.utils.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class TimestampSequenceGenerator {
    private static final AtomicInteger sequence = new AtomicInteger(100000);
    private static final int MAX_SEQUENCE = 999999;
    private static final int RESET_SEQUENCE = 100001;
    private static final ThreadLocal<DateTimeFormatter> formatter =
        ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyDDDHHmm"));

    public static long generateUniqueId() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int nextSequence = sequence.updateAndGet(current -> 
                current >= MAX_SEQUENCE ? RESET_SEQUENCE : current + 1);
                
            return Long.parseLong(now.format(formatter.get()) + 
                String.format("%06d", nextSequence));
        } finally {
            formatter.remove();
        }
    }

    public static String generateUniqueIdInString() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int nextSequence = sequence.updateAndGet(current ->
                    current >= MAX_SEQUENCE ? RESET_SEQUENCE : current + 1);

            return now.format(formatter.get()) +
                    String.format("%06d", nextSequence);
        } finally {
            formatter.remove();
        }
    }
}