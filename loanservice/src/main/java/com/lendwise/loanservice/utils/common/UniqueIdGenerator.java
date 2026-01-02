package com.lendwise.loanservice.utils.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bishranta Bhattarai
 * @version v1.0
 * @since 2024-06-18
 **/

public class UniqueIdGenerator {
    private static final AtomicInteger sequence = new AtomicInteger(100000);
    private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy");
    private static final DateTimeFormatter dayOfYearFormatter = DateTimeFormatter.ofPattern("DDD");
    private static final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH");
    private static final DateTimeFormatter minuteFormatter = DateTimeFormatter.ofPattern("mm");

    public static long generateUniqueId() {
        LocalDateTime now = LocalDateTime.now();

        String year = now.format(yearFormatter);
        String dayOfYear = now.format(dayOfYearFormatter);
        String hour = now.format(hourFormatter);
        String minute = now.format(minuteFormatter);

        int nextSequence = sequence.incrementAndGet();
        if (nextSequence > 999999) {
            sequence.set(100001);
            nextSequence = 100001;
        }

        String uniqueIdString = String.format("%s%s%s%s%06d",
            year,
            padLeft(dayOfYear, 3),
            padLeft(hour, 2),
            padLeft(minute, 2),
            nextSequence);

        return Long.parseLong(uniqueIdString);
    }

    private static String padLeft(String input, int length) {
        return String.format("%0" + length + "d", Integer.parseInt(input));
    }

}
