package com.earnstone.perf;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.junit.Test;

import com.earnstone.perf.PerfUtils.TimePrecision;

public class LastAccessTimeCounterTest {

	@Test
	public void basicTest() throws InterruptedException {
		LastAccessTimeCounter c = new LastAccessTimeCounter();

		Assert.assertEquals(-1.0, c.getValue());

		c.updateLastAccess();
		Thread.sleep(100);
		Assert.assertTrue(c.getValue() > 10);

		c.increment(); // increment does the same action as updateLastAccess()
		Thread.sleep(100);
		Assert.assertTrue(c.getValue() > 10);

		c.set(System.currentTimeMillis() - 10000);
		Thread.sleep(100);
		Assert.assertTrue(c.getValue() > 10000);

		c.setTimePrecision(TimePrecision.Sec);
		Assert.assertTrue(c.getValue() >= 10);
		Assert.assertTrue(c.getValue() < 20);

		c.setTimePrecision(TimePrecision.Min);
		Assert.assertTrue(c.getValue() >= .1667);
		Assert.assertTrue(c.getValue() < 1);		
	}

	@Test
	public void multiThreadedTest() throws InterruptedException {
		
		final AtomicBoolean failed = new AtomicBoolean(false);
		final LastAccessTimeCounter c = new LastAccessTimeCounter();
		final AtomicLong index = new AtomicLong(0);
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						long currentIndex = index.incrementAndGet();
						while (currentIndex < 2000000) {
							c.updateLastAccess();

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
