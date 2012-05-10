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

import java.util.concurrent.atomic.AtomicLong;

/**
 * A performance counter that counts. Use simple operations to increment and
 * decrement the raw count value. This class is considered thread safe.
 * 
 * @author Corey Hulen
 * 
 */
public class IncrementCounter extends Counter {

	protected AtomicLong count = new AtomicLong();	
	
	@Override
	public double getValue() {
		return count.get();
	}
	
	public String getDisplayValue() {
		return Long.toString(count.get());
	}
		
	public long increment() {
		return count.incrementAndGet();
	}
	
	public long decrement() {
		return count.decrementAndGet();
	}
	
	public long incrementBy(long add) {
		return count.addAndGet(add);
	}
	
	public long decrementBy(long sub) {
		return count.addAndGet(-sub);
	}
	
	public void set(long value) {
		count.set(value);
	}
}
