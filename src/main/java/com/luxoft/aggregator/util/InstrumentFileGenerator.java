package com.luxoft.aggregator.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class InstrumentFileGenerator {

	private static final int BATCH_SIZE = 10000;
	private static final NumberFormat formatter = new DecimalFormat("#0.00000");
//	private static boolean loggingEnabled = false; // Turn off logging by default
private static final AtomicBoolean loggingEnabled = new AtomicBoolean(false); // Turn off logging by default

	public static void generateInstrument(String filePath, long count) throws IOException {
		File fout = new File(filePath);
		FileOutputStream fos = new FileOutputStream(fout);

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
			
//			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			DateUtil.DateRange dateRange = DateUtil.getDefaultDateRange();
			Date fromDate = dateRange.getFromDate();
			Date toDate = dateRange.getToDate();

//			DateUtil2.DateRange dateRange = DateUtil2.getDefaultDateRange();
//			LocalDateTime fromDate = dateRange.getFromDate();
//			LocalDateTime toDate = dateRange.getToDate();

			int counter = 1;
			String instrument = "";
//			for (int i = 1; i <= 10; i++) {
//			for (int i = 1; i <= 200; i++) {
//			for (int i = 1; i <= 5000; i++) {
//			for (int i = 1; i <= 1000; i++) {
//			for (long i = 1; i <= 10000000; i++) { // 330 mb
			for (long i = 1; i <= count ; i++) { // 1.5 gb
				instrument = "INSTRUMENT" + counter;
				
				if (counter > 3){
					if (i % 100 == 0){
						counter = 0;
					}
					if (i % 5 == 0){
						instrument = "INSTRUMENT" + InstrumentUtil.generateRandomNumberInteger(1, 100);
					}
					counter++;
				} else {
					if (i % 100 == 0){
						counter++;
					}
				}
					
				String date = InstrumentUtil.generateRandomDateRange(fromDate, toDate);
				Double number = Double.parseDouble(formatter.format(InstrumentUtil.generateRandomNumberDouble(0, 100)));
				String line = instrument + "," + date + "," + number;
				log("\n" + "instrumentGenerated:" + line + "\n");
//				showFileSize(fout);
				
				bw.write(line);
				bw.newLine();
			}
		}
	}

	public static void generateInstrumentFunctional(String filePath, long count) {
		DateUtil.DateRange dateRange = DateUtil.getDefaultDateRange();
		Date fromDate = dateRange.getFromDate();
		Date toDate = dateRange.getToDate();

		AtomicInteger counter = new AtomicInteger(1);

		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

			Stream.iterate(1L, i -> i + 1)
					.limit(count)
					.map(i -> {
						String instrument = "INSTRUMENT" + counter.get();
						if (counter.get() > 3) {
							if (i % 100 == 0) {
								counter.set(0);
							}
							if (i % 5 == 0) {
								instrument = "INSTRUMENT" + InstrumentUtil.generateRandomNumberInteger(1, 100);
							}
							counter.incrementAndGet();
						} else {
							if (i % 100 == 0) {
								counter.incrementAndGet();
							}
						}
						String date = InstrumentUtil.generateRandomDateRange(fromDate, toDate);
						Double number = Double.parseDouble(formatter.format(InstrumentUtil.generateRandomNumberDouble(0, 100)));
						return instrument + "," + date + "," + number;
					})
					.forEach(line -> {
						log("\n" + "instrumentGenerated: " + line + "\n");

						try {
							bw.write(line);
							bw.newLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void generateInstrumentOptimized(String filePath, long count) throws IOException {
		DateUtil.DateRange dateRange = DateUtil.getDefaultDateRange();
		Date fromDate = dateRange.getFromDate();
		Date toDate = dateRange.getToDate();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
			StringBuilder batch = new StringBuilder();
			for (long i = 1; i <= count; i++) {
				String instrument = "INSTRUMENT" + (i % 4 == 0 ? InstrumentUtil.generateRandomNumberInteger(1, 100) : (i % 100 + 1));
				String date = InstrumentUtil.generateRandomDateRange(fromDate, toDate);
				Double number = Double.parseDouble(formatter.format(InstrumentUtil.generateRandomNumberDouble(0, 100)));

				String line = instrument + "," + date + "," + number;
				batch.append(line).append("\n");
				log("\n" + "instrumentGenerated: " + line + "\n");

				if (i % BATCH_SIZE == 0) {
					bw.write(batch.toString());
					batch.setLength(0);
				}
			}
			if (batch.length() > 0) {
				bw.write(batch.toString());
			}
		}
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

	// Enable or disable logging
	public static void setLoggingEnabled(boolean enabled) {
		loggingEnabled.set(enabled);
	}

	private static void log(String message) {
		if (loggingEnabled.get()) {
			System.out.println(message);
		}
	}

	public static void main(String[] args) {
		try {
			int[] sizesInMB = new int[] { 5, 10, 100, 300, 1024 };
			setLoggingEnabled(false);

			for (int sizeInMB : sizesInMB) {
				List<CompletableFuture<Void>> futures = new ArrayList<>();

				for (int i = 1; i <= 3; i++) {
					final int fileIndex = i; // to use inside lambda
					String filePath = "src/main/resources/instrument_test_input_" + sizeInMB + "MB_" + fileIndex + ".txt";
					long count = FileSizeCalculator.calculateCountForSize(sizeInMB);

					futures.add(CompletableFuture.runAsync(() -> {
						try {
							switch (fileIndex) {
								case 1:
									InstrumentFileGenerator.generateInstrument(filePath, count);
									break;
								case 2:
									InstrumentFileGenerator.generateInstrumentFunctional(filePath, count);
									break;
								case 3:
									InstrumentFileGenerator.generateInstrumentOptimized(filePath, count);
									break;
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}).thenRun(() -> {
						System.out.println("File generated: " + filePath);
					}));
				}

				CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
				allOf.join();

				System.out.println("Files generated for size: " + sizeInMB + "MB");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
