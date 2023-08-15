package com.luxoft.aggregator.util;

import java.io.UnsupportedEncodingException;

public class FileSizeCalculator {

    // Sample line: "INSTRUMENTX,2000-01-01 00:00:00,0.00000"
    private static final String SAMPLE_LINE = "INSTRUMENTX,2000-01-01 00:00:00,0.00000";

    // 1 MB in bytes
    private static final long ONE_MB = 1_048_576L;

    // Calculate the size of the sample line in bytes
    private static int getSampleLineSize() {
        int sampleLineSize;
        try {
            sampleLineSize = SAMPLE_LINE.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
        return sampleLineSize;
    }

    // Calculate the count of lines needed for the desired file size in MB
    public static long calculateCountForSize(int sizeInMB) {
        int sampleLineSize = getSampleLineSize();
        if (sampleLineSize == -1) {
            throw new IllegalStateException("Failed to calculate sample line size.");
        }
        long desiredSizeInBytes = sizeInMB * ONE_MB;
        return desiredSizeInBytes / sampleLineSize;
    }

    public static void main(String[] args) {
        System.out.println("Count for 5MB: " + calculateCountForSize(5));
        System.out.println("Count for 10MB: " + calculateCountForSize(10));
        System.out.println("Count for 100MB: " + calculateCountForSize(100));
        System.out.println("Count for 300MB: " + calculateCountForSize(300));
        System.out.println("Count for 1GB: " + calculateCountForSize(1024));
    }
}
