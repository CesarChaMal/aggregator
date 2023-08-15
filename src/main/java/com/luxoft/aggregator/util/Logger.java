package com.luxoft.aggregator.util;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class Logger {

    private static final AtomicBoolean loggingEnabled = new AtomicBoolean(false); // Turn off logging by default

    public static void log(String message) {
        if (loggingEnabled.get()) {
            System.out.println(message);
        }
    }

    public static void forceLog(String message) {
        System.out.println(message);
    }

    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled.set(enabled);
    }

    public static void showFileSize(File file) {
        if (file.exists()) {
            double bytes = file.length();
            double kilobytes = bytes / 1024;
            double megabytes = bytes / (1024 * 1024);
            double gigabytes = bytes / (1024 * 1024 * 1024);

            System.out.printf("File size: %.2f bytes\n", bytes);

            if (kilobytes > 1) {
                System.out.printf("File size: %.2f KB\n", kilobytes);
            }
            if (megabytes > 1) {
                System.out.printf("File size: %.2f MB\n", megabytes);
            }
            if (gigabytes > 1) {
                System.out.printf("File size: %.2f GB\n", gigabytes);
            }
        } else {
            System.out.println("File does not exist!");
        }
    }

}
