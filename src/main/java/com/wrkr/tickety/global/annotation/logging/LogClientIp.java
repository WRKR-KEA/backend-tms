package com.wrkr.tickety.global.annotation.logging;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogClientIp {

    ActionType action();
}
