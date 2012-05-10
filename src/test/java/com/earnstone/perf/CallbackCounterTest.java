package com.earnstone.perf;

import junit.framework.Assert;

import org.junit.Test;

public class CallbackCounterTest implements CallbackCounter.CounterUpdate {

	int index = 1;
	
	@Test
	public void basicTest() {
		CallbackCounter c = new CallbackCounter();	
		c.setCounterUpdate(this);
		
		Assert.assertEquals(1.0, c.getValue());		
		Assert.assertEquals(2.0, c.getValue());
	}	

	public void update(Counter counter) {
		counter.setValue(index);
		index++;
	}
}
