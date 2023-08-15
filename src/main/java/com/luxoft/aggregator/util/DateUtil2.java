package com.luxoft.aggregator.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateUtil2 {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final String from = "2000-01-01 00:00:00";
    private static final String to = "2023-01-01 00:00:00";

    public static DateRange parseDateRange(String from, String to) {
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        try {
            fromDate = LocalDateTime.parse(from, dtf);
            toDate = LocalDateTime.parse(to, dtf);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }

        return new DateRange(fromDate, toDate);
    }

    public static DateRange getDefaultDateRange() {
        return parseDateRange(from, to);
    }

    public static class DateRange {
        private final LocalDateTime fromDate;
        private final LocalDateTime toDate;

        public DateRange(LocalDateTime fromDate, LocalDateTime toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public LocalDateTime getFromDate() {
            return fromDate;
        }

        public LocalDateTime getToDate() {
            return toDate;
        }
    }
}
