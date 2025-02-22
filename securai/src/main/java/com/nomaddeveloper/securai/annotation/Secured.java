package com.nomaddeveloper.securai.annotation;

import com.nomaddeveloper.securai.Field;
import com.nomaddeveloper.securai.SecuraiInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods or endpoints for AI-based security validation.
 * When applied to a Retrofit method, this triggers {@link SecuraiInterceptor}
 * to analyze and enforce security policies based on the specified request fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Secured {

    /**
     * Specifies which parts of the request should be analyzed for security purposes.
     * By default, all parts of the request are analyzed ({@link Field#ALL}).
     *
     * @return an array of fields to be included in the security analysis
     */
    Field[] fields() default {Field.ALL};
}