package com.platform.modules.audit.aspect;

import com.platform.modules.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for automatic audit tracking
 * Phase 3: Future enhancement to intercept @Auditable methods
 * and automatically create audit log entries
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;

    /**
     * This aspect will be implemented in Phase 3 to automatically track
     * entity changes based on @Auditable annotations.
     *
     * Planned functionality:
     * - Intercept @Auditable annotated methods
     * - Capture method parameters and return values
     * - Compare before/after entity state
     * - Call AuditService.logAction automatically
     */
    @AfterReturning(pointcut = "@annotation(com.platform.modules.audit.annotation.Auditable)")
    public void afterAuditableMethod(JoinPoint joinPoint) {
        log.debug("Audit aspect intercepted method: {}", joinPoint.getSignature().getName());
        // Implementation in Phase 3
    }
}
