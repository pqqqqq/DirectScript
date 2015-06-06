package com.pqqqqq.directscript.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Kevin on 2015-06-02.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Statement {

    String prefix() default "";

    String[] identifiers();

    String suffix() default "";

    ExecutionTime executionTime() default ExecutionTime.RUNTIME;

    enum ExecutionTime {
        RUNTIME, COMPILE, ALWAYS
    }
}
