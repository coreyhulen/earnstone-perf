package com.earnstone.perf;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.junit.Test;

import com.earnstone.perf.PerfUtils.TimePrecision;

public class AvgTimeCounterTest {

	@Test
	public void basicTest() throws InterruptedException {
		AvgTimeCounter c = new AvgTimeCounter();

		long startTime = System.currentTimeMillis();
		Thread.sleep(100);
		c.addTime(startTime);
		Assert.assertTrue(c.getValue() >= 100);

		Thread.sleep(100);
		c.addTime(startTime, System.currentTimeMillis());
		Assert.assertTrue(c.getValue() >= 100);

		c.setTimePrecision(TimePrecision.Sec);
		Assert.assertTrue(c.getValue() >= .1);
		Assert.assertTrue(c.getValue() < .2);		
	}

	@Test
	public void multiThreadedTest() throws InterruptedException {

		final AtomicBoolean failed = new AtomicBoolean(false);
		final AvgTimeCounter c = new AvgTimeCounter();
		final AtomicLong index = new AtomicLong(0);
		final long startTime = System.currentTimeMillis();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						long currentIndex = index.incrementAndGet();
						while (currentIndex < 2000000) {
							c.addTime(startTime);

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
