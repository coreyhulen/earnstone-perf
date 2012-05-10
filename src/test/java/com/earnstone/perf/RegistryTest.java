package com.earnstone.perf;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class RegistryTest {
	
	@Test
	public void basicTest() {
		
		Registry.setShouldRegisterJmx(true);
		
		IncrementCounter incrementCounter = new IncrementCounter();		
		incrementCounter.setCategory("RegistryTest");
		incrementCounter.setGroup("group1");
		incrementCounter.setName("IncrementCounter");
		Registry.register(incrementCounter);
		incrementCounter.increment();
		
		PercentCounter percentCounter = new PercentCounter();
		percentCounter.setCategory("RegistryTest");
		percentCounter.setGroup("group2");
		percentCounter.setName("PercentCounter");
		Registry.register(percentCounter);
		percentCounter.increment();
		percentCounter.incrementBase();
		percentCounter.incrementBase();
		
		Counter rIncrementCounter = Registry.getCounter(incrementCounter.getCategory(), incrementCounter.getGroup(), incrementCounter.getName());
		Assert.assertNotNull(rIncrementCounter);
		
		List<Counter> counters1 = Registry.listCounters(null, null);
		Assert.assertEquals(2, counters1.size());
		
		List<Counter> counters2 = Registry.listCounters("RegistryTest", null);
		Assert.assertEquals(2, counters2.size());
		
		List<Counter> counters3 = Registry.listCounters("RegistryTest", "group1");
		Assert.assertEquals(1, counters3.size());
		
		List<String> categories = Registry.listCategories();
		Assert.assertEquals(1, categories.size());
		
		List<String> groups = Registry.listGroupsForCategories("RegistryTest");
		Assert.assertEquals(2, groups.size());
	}
}
