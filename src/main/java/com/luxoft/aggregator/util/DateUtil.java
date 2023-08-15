package com.luxoft.aggregator.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US));
    private static final String from = "2000-01-01 00:00:00";
    private static final String to = "2023-01-01 00:00:00";

    public static DateRange parseDateRange(String from, String to) {
        Date fromDate = null;
        Date toDate = null;

        try {
            fromDate = sdf.get().parse(from);
            toDate = sdf.get().parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new DateRange(fromDate, toDate);
    }

    public static DateRange getDefaultDateRange() {
        return parseDateRange(from, to);
    }

    public static class DateRange {
        private final Date fromDate;
        private final Date toDate;

        public DateRange(Date fromDate, Date toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public Date getFromDate() {
            return fromDate;
        }

        public Date getToDate() {
            return toDate;
        }
    }
}
