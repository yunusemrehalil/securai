package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Enum representing interceptor messages.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum InterceptorMessage {

    CLASSIFIER_ERROR("Classifier error. Proceeding request: "),
    CLASSIFIER_ERROR_BLOCKED("Classifier error. Blocking request: "),
    INTERCEPTING_REQUEST("Intercepting request: "),
    SECURITY_THREAT_DETECTED("Security threat detected! Blocking request: ");

    private final String message;

    InterceptorMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}