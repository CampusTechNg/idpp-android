package com.campustechng.aminu.idpenrollment.sourceafis.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {
	// default precision -1 means auto: 0 for integers, 2 for floats
	int precision() default -1;

	double lower() default 0;

	double upper() default 0;
}
