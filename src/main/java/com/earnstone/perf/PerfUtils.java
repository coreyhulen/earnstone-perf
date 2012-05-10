package com.earnstone.perf;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class PerfUtils {

	public static final int DoublePercision = 3;
	public static final String FormatDefault = "0.###";
	public static final NumberFormat Formatter = new DecimalFormat(FormatDefault);

	public static final double MillisInSec = 1000;
	public static final double MillisInMin = 60 * MillisInSec;
	public static final double MillisInHour = 60 * MillisInMin;
	public static final double MillisInDay = 24 * MillisInHour;

	public enum TimePrecision {
		Milli, Sec, Min, Hour, Day
	}
	
	public static double round(double value) {
		if (Double.isNaN(value))
			return value;
		
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(DoublePercision, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	public static double getMillisForPrecision(TimePrecision tp) {
				
		if (tp.equals(TimePrecision.Sec))
			return MillisInSec;
		else if (tp.equals(TimePrecision.Min))
			return MillisInMin;
		else if (tp.equals(TimePrecision.Hour))
			return MillisInHour;
		else if (tp.equals(TimePrecision.Day))
			return MillisInDay;
		else
			return 1;
	}
}
