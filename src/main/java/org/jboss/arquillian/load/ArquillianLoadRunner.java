package org.jboss.arquillian.load;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.jboss.arquillian.load.annotation.LoopCount;
import org.jboss.arquillian.load.annotation.MaxThreadCount;
import org.jboss.arquillian.load.annotation.RampUpDelay;
import org.jboss.arquillian.load.load.ArquillianRunnable;
import org.jboss.arquillian.load.load.RampupRunnable;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class ArquillianLoadRunner extends BlockJUnit4ClassRunner {

	private final ScheduledExecutorService testing;
	private final ScheduledExecutorService rampup;

	private final Class runClass;
	private final int threadCount;
	private final int loopCount;
	private final long rampupDelay;
	private final TimeUnit rampupTimeUnit;
	
	public ArquillianLoadRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.runClass = klass;
		
		if(klass.isAnnotationPresent(MaxThreadCount.class)) {
			MaxThreadCount tc = klass.getAnnotation(MaxThreadCount.class);
			threadCount = tc.value();
			System.out.println("Setting threads: "+threadCount);
		}
		else {
			System.out.println("Setting threads default: 1");
			threadCount = 1;
		}
		
		if(klass.isAnnotationPresent(LoopCount.class)) {
			System.out.println("Found loop count: "+klass.getCanonicalName());
			LoopCount ct = klass.getAnnotation(LoopCount.class);
			loopCount = ct.value();
		}
		else {
			loopCount = 1;
		}
		

		if(klass.isAnnotationPresent(RampUpDelay.class)) {
			RampUpDelay rampup = klass.getAnnotation(RampUpDelay.class);
			rampupDelay = rampup.value();
			rampupTimeUnit = rampup.timeunit();
		}
		else {
			rampupDelay = 0;
			rampupTimeUnit = TimeUnit.SECONDS;
		}
		
		
		if(rampupDelay <= 0) 
		{
			testing = Executors.newScheduledThreadPool(threadCount);
		}
		else {
			testing = Executors.newScheduledThreadPool(1);
		}
		rampup = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void run(RunNotifier notifier) {

		StopWatch stopwatch = new StopWatch();
		
		CountDownLatch latch = new CountDownLatch(loopCount);
		Runnable loopInstance;
		try {
			
			if(rampupDelay > 0) {
				loopInstance = new RampupRunnable((ScheduledThreadPoolExecutor)testing, threadCount, stopwatch);
				rampup.scheduleAtFixedRate(loopInstance, rampupDelay, rampupDelay, rampupTimeUnit);
			}
			
			for(int i=0; i < loopCount; i++) {
				loopInstance = new ArquillianRunnable(this.runClass, notifier, latch);
				testing.schedule(loopInstance, 0, TimeUnit.SECONDS);
			}
			
		} catch (InitializationError e) {
			throw new RuntimeException("holy moly", e);
		} 
		
		stopwatch.start();

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("holy.", e);
		}
		
		stopwatch.stop();
		System.out.println(DurationFormatUtils.formatDurationHMS(stopwatch.getTime()));
	}

}
