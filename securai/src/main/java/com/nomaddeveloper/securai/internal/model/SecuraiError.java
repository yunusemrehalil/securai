package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

import com.nomaddeveloper.securai.internal.exception.SecuraiException;

/**
 * Enum representing various error messages for Securai.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum SecuraiError {

    UNKNOWN_ERROR("Unknown classification error occurred."),
    XSS_CLASSIFIER_INIT_FAILED("Failed to initialize XSS classifier."),
    THREAD_INTERRUPTED("Thread interrupted while waiting for XSS classification."),
    XSS_CLASSIFIER_NOT_INITIALIZED("XSS classifier is not initialized."),
    NO_FIELD_VALUE_PROVIDED("No field value provided for classification."),
    XSS_CATEGORY_MISSING("Category with index '1' (XSS) not found."),
    SECURITY_THREAT_DETECTED("Request contains a security threat"),
    THRESHOLD_IS_NOT_IN_BOUND("Threshold must be between 0 and 1"),
    CONTEXT_MUST_NOT_BE_NULL("Context must not be null.");

    private final String message;

    SecuraiError(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public SecuraiException toException() {
        return new SecuraiException(message);
    }

    public SecuraiException toException(final Throwable cause) {
        return new SecuraiException(message, cause);
    }
}
