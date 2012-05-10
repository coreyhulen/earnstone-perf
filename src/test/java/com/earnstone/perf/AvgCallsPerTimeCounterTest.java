package com.earnstone.perf;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.junit.Test;

public class AvgCallsPerTimeCounterTest {

	@Test
	public void basicTest() throws InterruptedException {
		AvgCallsPerTimeCounter c = new AvgCallsPerTimeCounter();

		Assert.assertEquals(Double.NaN, c.getValue());

		c.incrementCall();
		Assert.assertEquals(Double.NaN, c.getValue());

		Thread.sleep(100);
		c.incrementCall();
		Assert.assertTrue(c.getValue() >= .02);				
	}

	@Test
	public void multiThreadedTest() throws InterruptedException {

		final AtomicBoolean failed = new AtomicBoolean(false);
		final AvgCallsPerTimeCounter c = new AvgCallsPerTimeCounter();
		final AtomicLong index = new AtomicLong(0);		
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						long currentIndex = index.incrementAndGet();
						while (currentIndex < 2000000) {
							c.incrementCall();							

							if (currentIndex % 10000 == 0)
								c.getValue();

							currentIndex = index.incrementAndGet();
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
