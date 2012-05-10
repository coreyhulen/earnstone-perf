package com.earnstone.perf;

import com.earnstone.perf.PerfUtils.TimePrecision;

/**
 * A performance counter for calls per second over a time sample. The big
 * difference between this class and its base <code>AvgCounter</code> are some
 * convenience methods to display the results as calls per second (or minutes,
 * hours, etc). This class is considered thread-safe. If there are more calls to
 * <code>incrementCall()</code> than the sample size with in the
 * <code>PerTime</code> then the value returns 'NaN' or essentially its too fast
 * increase the sample size buffer. Meaning if I have a sample size of 10 with
 * <code>PerTime.Sec</code> set and I <code>incrementCall</code> more than 10
 * times in a second then the results cannot be calculated. We only know you are
 * at 10+ request per second (so we show NaN).
 * 
 * @author Corey Hulen
 * 
 */
public class AvgCallsPerTimeCounter extends AvgCounter {

	private TimePrecision timePrecision = TimePrecision.Milli;

	public void setTimePrecision(TimePrecision timePrecision) {
		this.timePrecision = timePrecision;
	}

	public TimePrecision getTimePrecision() {
		return timePrecision;
	}

	@Override
	public String getDisplayValue() {
		synchronized (PerfUtils.Formatter) {
			return PerfUtils.Formatter.format(getValue());
		}
	}

	/**
	 * A convenience method for adding the current time to the underlying array.
	 * Internally it calls <code>addValue</code> from the base class.
	 */
	public void incrementCall() {
		addValue(System.currentTimeMillis());
	}

	@Override
	public double getValue() {
		return PerfUtils.round(callsPerTime());
	}
	
	private double callsPerTime() {

		double minCount = 0;
		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;

		for (int i = 0; i < items.length(); i++) {
			long raw = items.get(i);

			if (raw == Long.MIN_VALUE)
				minCount++;
			else {
				max = Math.max(max, raw);
				min = Math.min(min, raw);
			}
		}

		if (minCount == items.length())
			return Double.NaN;

		double period = (double) (max - min) / PerfUtils.getMillisForPrecision(timePrecision);

		if (period <= 0)
			return Double.NaN;
		else
			return (double) (items.length() - minCount) / period;
	}
}
