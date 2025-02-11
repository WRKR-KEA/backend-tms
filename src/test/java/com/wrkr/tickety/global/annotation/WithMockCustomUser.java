package com.wrkr.tickety.global.annotation;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "testUser";

    Role role() default Role.USER;

    String nickname() default "alex.js";
}
