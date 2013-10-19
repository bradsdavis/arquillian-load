package org.jboss.arquillian.load.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RampUpDelay {
	long value() default 0;
	TimeUnit timeunit() default TimeUnit.SECONDS; 
}
