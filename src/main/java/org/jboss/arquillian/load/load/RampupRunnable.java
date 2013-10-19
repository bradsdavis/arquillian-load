package org.jboss.arquillian.load.load;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;

public class RampupRunnable implements Runnable {
	
	private final int maxThreads;
	private final ScheduledThreadPoolExecutor threadPoolExecutor;
	private final StopWatch stopWatch;
	
	public RampupRunnable(ScheduledThreadPoolExecutor tpe, int max, StopWatch sw) {
		this.threadPoolExecutor = tpe;
		this.maxThreads = max;
		this.stopWatch = sw;
	}
	
	public void run() {
		if(this.maxThreads > 0) {
			int current = threadPoolExecutor.getCorePoolSize();
			if(this.maxThreads > current) {
				System.out.println("Ramping up threads to : "+ (current+1)+ " at time: "+DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));
				threadPoolExecutor.setCorePoolSize(threadPoolExecutor.getCorePoolSize()+1);
			}
			else {
				System.out.println("RampUp completed to: "+maxThreads+" threads.");
				throw new Error("Rampup complete!");
			}
		}
	}
}
