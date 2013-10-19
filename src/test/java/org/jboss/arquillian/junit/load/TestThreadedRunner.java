package org.jboss.arquillian.junit.load;

import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.load.ArquillianLoadRunner;
import org.jboss.arquillian.load.annotation.LoopCount;
import org.jboss.arquillian.load.annotation.MaxThreadCount;
import org.jboss.arquillian.load.annotation.RampUpDelay;
import org.junit.Test;
import org.junit.runner.RunWith;

@MaxThreadCount(20)
@LoopCount(200)
@RampUpDelay(value=5, timeunit=TimeUnit.SECONDS)
@RunWith(ArquillianLoadRunner.class)
public class TestThreadedRunner {

	@Test
	public void testOne() throws Exception {
		Thread.sleep(1000);
		System.out.println("Ran test!");
	}
}
