package com.digital.mecommerces.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditingAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditingAspect.class);

    @After("execution(* com.digital.mecommerces.controller.*.*(..)) && !execution(* com.digital.mecommerces.controller.AuthController.*(..))")
    public void logAfterControllerCall(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "Anonymous";

        logger.info("Usuario '{}' llamó a {}.{}()",
                username,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }
}

