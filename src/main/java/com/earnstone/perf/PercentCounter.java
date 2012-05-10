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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A performance counter that calculates percent. Use simple operations to
 * increment and decrement the raw numerator and denominator values. This class
 * is considered thread safe. Great for hit cache performance counters.
 * 
 * @author Corey Hulen
 * 
 */
public class PercentCounter extends IncrementCounter {

	protected AtomicLong base = new AtomicLong();
	protected boolean inverse;
	
	public static final String FORMAT_DEFAULT = "#0.0%";
	public static final NumberFormat formatter = new DecimalFormat(FORMAT_DEFAULT);	
	
	/**
	 * True if the percent value should be inverted. The default is false. So
	 * 25% would become 75%.
	 */
	public boolean getInverse() {
		return inverse;
	}

	/**
	 * Set to true to invert the percent value.
	 */
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}
		
	public long incrementBase() {
		return base.incrementAndGet();
	}
	
	public long incrementBaseBy(long add) {
		return base.addAndGet(add);
	}
	
	public long decrementBase() {
		return base.decrementAndGet();
	}		
	
	public long decrementBaseBy(long sub) {
		return base.addAndGet(-sub);
	}
		
	public long getBase() {		
		return base.get();
	}
		
	public void setBase(long value) {
		base.set(value);
	}
	
	@Override
	public double getValue() {

		double tempBase = base.get();
		double tempCount = count.get();

		double percent = tempBase == 0 ? 0.0 : tempCount / tempBase;

		if (inverse)
			percent = 1 - percent;

		return PerfUtils.round(percent);
	}
	
	@Override
	public String getDisplayValue() {
		synchronized (formatter) {
			return formatter.format(getValue());
		}
	}
}
