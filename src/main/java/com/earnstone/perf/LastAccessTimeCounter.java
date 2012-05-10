/*
 * ePerf: Earnstone Performance Counters.
 * 
 * Copyright 2011 Corey Hulen, Earnstone Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.earnstone.perf;

import com.earnstone.perf.PerfUtils.TimePrecision;

/**
 * A performance counter for displaying last access time. The big difference
 * between this class and its base <code>PerfCounterCount</code> are some
 * convenience methods to display the results as time. This class is considered
 * thread-safe.
 * 
 * @author Corey Hulen
 * 
 */
public class LastAccessTimeCounter extends IncrementCounter {

	private TimePrecision timePrecision = TimePrecision.Milli;
	
	public LastAccessTimeCounter() {
		count.set(Long.MIN_VALUE);
	}
	
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
	
	@Override
	public double getValue() {		
		if (count.get() == Long.MIN_VALUE)
			return -1;
				
		return PerfUtils.round((double)(System.currentTimeMillis() - count.get()) / PerfUtils.getMillisForPrecision(timePrecision));			
	}

	/**
	 * A convenience method to set the current value to
	 * <code>System.currentTimeMillis()</code>.
	 */
	public void updateLastAccess() {
		count.set(System.currentTimeMillis());
	}	
	
	@Override
	public long increment() {
		count.set(System.currentTimeMillis());
		return count.get();
	}
	
	@Override
	public long decrement() {		
		return count.get();
	}
}
