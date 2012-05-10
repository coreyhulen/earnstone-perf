package com.earnstone.perf;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

public class IncrementCounterTest {

	@Test
	public void basicTest() {
		IncrementCounter c = new IncrementCounter();

		c.increment();
		Assert.assertEquals(1.0, c.getValue());

		c.decrement();
		Assert.assertEquals(0.0, c.getValue());

		c.incrementBy(10);
		Assert.assertEquals(10.0, c.getValue());

		c.decrementBy(5);
		Assert.assertEquals(5.0, c.getValue());		
	}

	@Test
	public void multiThreadedTest() throws InterruptedException {

		final AtomicBoolean failed = new AtomicBoolean(false);
		final IncrementCounter c = new IncrementCounter();
		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						while (c.getValue() < 20000000)
							c.increment();
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
