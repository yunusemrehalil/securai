package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Enum representing interceptor messages.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum InterceptorMessage {

    INITIAL_ERROR("Initial error"),
    INTERCEPTING_REQUEST("Intercepting request: "),
    SECURITY_ANALYSIS_TIMEOUT("Security analysis timeout. Proceeding request: "),
    SECURITY_THREAT_DETECTED("Security threat detected! Blocking request: "),
    THREAD_INTERRUPTED("Interceptor thread interrupted.");

    private final String message;

    InterceptorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}