package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Enum representing interceptor messages.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum InterceptorMessage {

    CLASSIFIER_ERROR("Classifier error. Proceeding request: "),
    CLASSIFIER_ERROR_BLOCKED("Classifier error. Blocking request: "),
    INITIAL_ERROR("Classification did not complete"),
    INTERCEPTING_REQUEST("Intercepting request: "),
    SECURITY_ANALYSIS_TIMEOUT("Security analysis timeout. Proceeding request: "),
    SECURITY_ANALYSIS_TIMEOUT_BLOCKED("Security analysis timeout. Blocking request: "),
    SECURITY_THREAT_DETECTED("Security threat detected! Blocking request: "),
    THREAD_INTERRUPTED("Interceptor thread interrupted. Proceeding request: "),
    THREAD_INTERRUPTED_BLOCKED("Interceptor thread interrupted. Blocking request: ");

    private final String message;

    InterceptorMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}