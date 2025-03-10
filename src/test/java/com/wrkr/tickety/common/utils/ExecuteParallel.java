package com.wrkr.tickety.common.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Execution(ExecutionMode.CONCURRENT)
public @interface ExecuteParallel {

}
