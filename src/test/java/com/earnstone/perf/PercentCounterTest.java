package com.earnstone.perf;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

public class PercentCounterTest {

	@Test
	public void basicTest() {
		PercentCounter c = new PercentCounter();

		c.increment();
		c.incrementBase();
		Assert.assertEquals(1.0, c.getValue());

		c.decrement();
		c.decrementBase();
		Assert.assertEquals(0.0, c.getValue());

		c.incrementBy(10);
		c.incrementBaseBy(10);
		Assert.assertEquals(1.0, c.getValue());

		c.setInverse(true);
		Assert.assertEquals(0.0, c.getValue());

		c.setInverse(false);
		c.decrementBy(5);
		Assert.assertEquals(0.5, c.getValue());
		Assert.assertEquals("50.0%", c.getDisplayValue());

		c.decrementBase();
		Assert.assertEquals(0.556, c.getValue());
		Assert.assertEquals("55.6%", c.getDisplayValue());		
	}

	@Test
	public void multiThreadedTest() throws InterruptedException {

		final AtomicBoolean failed = new AtomicBoolean(false);
		final PercentCounter c = new PercentCounter();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						while (c.getBase() < 40000000) {
							c.increment();
							c.incrementBaseBy(2);
						}
					}
					catch (Exception e) {
						failed.set(true);
					}
				}
			});

			threads.add(t);
			t.setDaemon(true);
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		if (failed.get())
			Assert.fail();
	}
}
