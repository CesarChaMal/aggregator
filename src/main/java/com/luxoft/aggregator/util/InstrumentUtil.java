package com.luxoft.aggregator.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InstrumentUtil {

	public static double generateRandomNumberDouble(int modifier_min, int modifier_max) {
		return modifier_min + (Math.random() * ((modifier_max - modifier_min)));
	} 
	
	public static int generateRandomNumberInteger(int modifier_min, int modifier_max) {
		return modifier_min + (int)(Math.random() * ((modifier_max - modifier_min) + 1));
	}
	
	public static String generateRandomDateRange(Date from, Date to) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long offset = Timestamp.valueOf(sdf.format(from)).getTime();
		long end = Timestamp.valueOf(sdf.format(to)).getTime();
		long diff = end - offset + 1;
		Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));

		DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
//		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date today = (Date) Calendar.getInstance().getTime();        
		String todayDate = df.format(today);
		String date = df.format(rand);

		System.out.println(date);
		System.out.println(today);
		return date;
	} 
	
}
