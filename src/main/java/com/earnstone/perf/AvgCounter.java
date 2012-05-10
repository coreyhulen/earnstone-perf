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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * A performance counter for averaging over a sample. The focus is on
 * performance and being able to add raw values quickly to the sample. Under
 * high loads values can be missed. This class is considered thread safe.
 * 
 * @author Corey Hulen
 * 
 */
public class AvgCounter extends Counter {

	protected AtomicInteger index = new AtomicInteger();
	protected AtomicLongArray items;

	public static final int DefaultSampleSize = 128;	

	public AvgCounter() {
		items = new AtomicLongArray(DefaultSampleSize);
		initializeArray(items);
	}

	/**
	 * Resizes the samples size to save. This will remove all the existing data
	 * and reset the array. This method is not considered thread safe but since
	 * it's not called often or only on initialize we still allow it.
	 * 
	 * @param sampleSize
	 *            The size of the new sample to hold in memory.
	 */
	public void setSampleSize(int sampleSize) {
		AtomicLongArray newArray = new AtomicLongArray(sampleSize);
		initializeArray(newArray);
		items = newArray;
		index.set(0);
	}
	
	public int getSampleSize() {
		return items.length();
	}

	private void initializeArray(AtomicLongArray initArray) {
		for (int i = 0; i < initArray.length(); i++) {
			initArray.set(i, Long.MIN_VALUE);
		}
	}

	@Override
	public String getDisplayValue() {
		synchronized (PerfUtils.Formatter) {
			return PerfUtils.Formatter.format(getValue());
		}
	}

	@Override
	public double getValue() {
		return PerfUtils.round(average());
	}

	/**
	 * Adds a value to the sample. Increments the index and adds the value to
	 * the array.
	 * 
	 * Under high load some samples may be missed. Increasing the sampleSize
	 * decreases the chance a sample will be missed.
	 * 
	 * @param raw
	 *            The value to add into the sample
	 */
	public void addValue(long raw) {

		int tempIndex = index.incrementAndGet();

		if (tempIndex <= items.length()) {
			items.set(tempIndex - 1, raw);
		}
		else {
			tempIndex = index.getAndSet(0);
			// If this thread won it should be zero.
			if (tempIndex == 0) {
				index.incrementAndGet();
				items.set(0, raw);
			}
			else {
				// recursion makes the most sense so we
				// don't miss any values something like
				// addValue(raw) but in reality missing
				// a sample or two is better than the
				// recursion endlessly looping.
				// If the method is hammered so hard we went
				// threw the array twice then missing samples
				// is the least of our worries.
				tempIndex = index.incrementAndGet();

				if (tempIndex <= items.length()) {
					items.set(tempIndex - 1, raw);
				}
			}
		}
	}

	public double average() {

		double sum = 0;
		long raw = 0;
		int i = 0;

		for (i = 0; i < items.length(); i++) {
			raw = items.get(i);

			if (raw != Long.MIN_VALUE)
				sum += raw;
			else
				break;
		}

		return i == 0 ? 0 : sum / i;
	}
}
