package com.earnstone.perf;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.earnstone.perf.PerfUtils.TimePrecision;

public class SampleUsageTest implements CallbackCounter.CounterUpdate {

	int callbackData = 1;

	@Test
	public void basicUsage() throws IOException {

		Registry.setShouldRegisterJmx(true);

		IncrementCounter incrementCounter = new IncrementCounter();
		incrementCounter.setCategory("Sample");
		incrementCounter.setGroup("group1");
		incrementCounter.setName("IncrementCounter");
		Registry.register(incrementCounter);
		incrementCounter.increment();

		PercentCounter percentCounter = new PercentCounter();
		percentCounter.setCategory("Sample");
		percentCounter.setGroup("group1");
		percentCounter.setName("PercentCounter");
		// percentCounter.setInverse(true);
		Registry.register(percentCounter);
		percentCounter.increment();
		percentCounter.incrementBase();
		percentCounter.incrementBase();

		LastAccessTimeCounter lastAccessTimeCounter = new LastAccessTimeCounter();
		lastAccessTimeCounter.setCategory("Sample");
		lastAccessTimeCounter.setGroup("group1");
		lastAccessTimeCounter.setName("LastAccessTimeCounter");
		lastAccessTimeCounter.setTimePrecision(TimePrecision.Sec);
		Registry.register(lastAccessTimeCounter);
		lastAccessTimeCounter.updateLastAccess();
		// same as lastAccessTimeCounter.increment()

		AvgCounter avgCounter = new AvgCounter();
		avgCounter.setCategory("Sample");
		avgCounter.setGroup("group2");
		avgCounter.setName("AvgCounter");
		// avgCounter.setSampleSize(256);
		Registry.register(avgCounter);
		avgCounter.addValue(200);

		long startTime = System.currentTimeMillis();
		AvgTimeCounter avgTimeCounter = new AvgTimeCounter();
		avgTimeCounter.setCategory("Sample");
		avgTimeCounter.setGroup("group2");
		avgTimeCounter.setName("AvgTimeCounter");
		// avgTimeCounter.setSampleSize(256);
		Registry.register(avgTimeCounter);
		avgTimeCounter.addTime(startTime);
		// same as avgTimeCounter.addTime(startTime,
		// System.currentTimeMillis());

		AvgCallsPerTimeCounter avgCallsPerTimeCounter = new AvgCallsPerTimeCounter();
		avgCallsPerTimeCounter.setCategory("Sample");
		avgCallsPerTimeCounter.setGroup("group2");
		avgCallsPerTimeCounter.setName("AvgCallsPerTimeCounter");
		// avgCallsPerTimeCounter.setSampleSize(256);
		Registry.register(avgCallsPerTimeCounter);
		avgCallsPerTimeCounter.incrementCall();
		
		CallbackCounter callbackCounter = new CallbackCounter();
		callbackCounter.setCounterUpdate(this);
		callbackCounter.setCategory("Sample");
		callbackCounter.setGroup("group2");
		callbackCounter.setName("CallbackCounter");
		Registry.register(callbackCounter);

		List<Counter> allCounters = Registry.listCounters(null, null);
		Registry.printPerfs(allCounters);

		List<Counter> allCountersInSampleCategory = Registry.listCounters("Sample", null);
		Registry.printPerfs(allCountersInSampleCategory);

		List<Counter> allCountersInSampleCategoryAndGroup1 = Registry.listCounters("Sample", "group1");
		Registry.printPerfs(allCountersInSampleCategoryAndGroup1);

		// Uncomment out the following lines to test connecting 
		// with Jconsole or by accessing
		// the html adapter at http://localhost:8083
		// Registry.registerJmxHtmlAdapter(8083);
		// System.out.println("System waiting (Conect with jconsole to see perf counters)");
		// System.out.println("Hit any key to continue...");
		// System.in.read();
	}

	public void update(Counter counter) {
		counter.setValue(callbackData);
		callbackData++;
	}
}
