package org.jboss.arquillian.load.load;

import java.util.concurrent.CountDownLatch;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class ArquillianRunnable implements Runnable {

	private final Arquillian arquillian;
	private final RunNotifier notifier;
	private final CountDownLatch latch;
	
	public ArquillianRunnable(Class clz, RunNotifier notifier, CountDownLatch latch) throws InitializationError {
		this.latch = latch;
		this.arquillian = new Arquillian(clz);
		this.notifier = notifier;
	}
	
	public void run() {
		arquillian.run(notifier);
		latch.countDown();
	}
}
