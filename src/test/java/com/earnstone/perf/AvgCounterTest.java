package com.earnstone.perf;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.junit.Test;

public class AvgCounterTest {

	@Test
	public void basicTest() {
		AvgCounter c = new AvgCounter();

		c.addValue(1);
		Assert.assertEquals(1.0, c.getValue());

		c.addValue(1);
		Assert.assertEquals(1.0, c.getValue());

		c.addValue(2);
		Assert.assertEquals(1.333, c.getValue());		
	}

	@Test
	public void revolveTest() {

		AvgCounter c = new AvgCounter();
		c.setSampleSize(4);

		for (int i = 0; i < c.getSampleSize() + 1; i++) {
			c.addValue(i);
		}

		Assert.assertEquals(2.5, c.getValue());
	}

	@Test
	public void multiThreadedTest() throws InterruptedException {

		final AtomicBoolean failed = new AtomicBoolean(false);
		final AvgCounter c = new AvgCounter();
		final AtomicLong index = new AtomicLong(0);
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						long currentIndex = index.incrementAndGet();
						while (currentIndex < 40000000) {
							c.addValue(currentIndex);

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
