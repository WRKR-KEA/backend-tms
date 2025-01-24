package com.wrkr.tickety.global.logging;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.wrkr.tickety..*Service.*(..)) || execution(* com.wrkr.tickety..*Controller.*(..))")
    public void method() {
    }

    @Around("method()")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.toString(joinPoint.getArgs());

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long ms = end - start;

        MDC.put("type", "method");
        MDC.put("value.class", className);
        MDC.put("value.method", methodName);
        MDC.put("value.params", params);
        MDC.put("value.total_ms", Long.toString(ms));

        log.info("Method Log");

        MDC.remove("type");
        MDC.remove("value.class");
        MDC.remove("value.method");
        MDC.remove("value.params");
        MDC.remove("value.total_ms");

        return result;
    }
}
