package com.platform.modules.audit.annotation;

import java.lang.annotation.*;

/**
 * Annotation for marking methods that should be audited.
 * Phase 3: AOP aspects will use this to automatically create audit logs
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String value() default "";
    String entityType() default "Entity";
}
