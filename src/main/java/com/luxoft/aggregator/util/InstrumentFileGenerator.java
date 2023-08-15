package com.luxoft.aggregator.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class InstrumentFileGenerator {

	public static void generateInstrument(String filePath, long count) throws IOException {
		File fout = new File(filePath);
		FileOutputStream fos = new FileOutputStream(fout);

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
			
//			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String from = "2000-01-01 00:00:00";
			String to = "2023-01-01 00:00:00";
			Date fromDate = null;
			Date toDate = null;
			NumberFormat formatter = new DecimalFormat("#0.00000");     

			try {
				fromDate = sdf.parse(from);
				toDate = sdf.parse(to);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

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
				String instrumentGenerated = instrument + "," + date + "," + number;
				System.out.println("\n" + "instrumentGenerated:" + instrumentGenerated + "\n");
				showFileSize(fout);
				
				bw.write(instrumentGenerated);
				bw.newLine();
			}
		}
	}

	public static void generateInstrumentFunctional(String filePath, long count) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String from = "2000-01-01 00:00:00";
		String to = "2023-01-01 00:00:00";
		NumberFormat formatter = new DecimalFormat("#0.00000");

		Date fromDate;
		Date toDate;
		try {
			fromDate = sdf.parse(from);
			toDate = sdf.parse(to);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

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
						System.out.println("\n" + "instrumentGenerated: " + line + "\n");
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

	public static void main(String[] args) {
		try {
			int[] sizesInMB = new int[] {5, 10, 100, 300, 1024};

			for(int sizeInMB : sizesInMB) {
				String filePath1 = "src/main/resources/very_huge_input_" + sizeInMB + "MB_1.txt";
				String filePath2 = "src/main/resources/very_huge_input_" + sizeInMB + "MB_2.txt";
				long count = FileSizeCalculator.calculateCountForSize(sizeInMB);

				CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
					try {
						InstrumentFileGenerator.generateInstrument(filePath1, count);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}).thenRun(() -> {
					System.out.println("File generated: " + filePath1);
				});

				CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
					InstrumentFileGenerator.generateInstrumentFunctional(filePath2, count);
				}).thenRun(() -> {
					System.out.println("File generated: " + filePath2);
				});

				CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);
				allOf.join();

				System.out.println("Files generated for size: " + sizeInMB + "MB");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
